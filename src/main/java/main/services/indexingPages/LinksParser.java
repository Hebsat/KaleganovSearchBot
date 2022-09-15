package main.services.indexingPages;

import main.services.lemmatization.LemmaCollector;
import main.model.LemmaValues;
import main.model.Field;
import main.model.Index;
import main.model.Page;
import main.properties.ParseProperties;
import main.repository.*;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.ConnectException;
import java.util.*;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.logging.Logger;

public class LinksParser extends RecursiveAction {

    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final ParseProperties properties;
    private final String domainName;
    private final Page page;
    private final Map<String, Float> lemmasRank;
    private final Map<String, String> lemmaPositionInText;
    private List<String> currentLinks;

    public LinksParser(SiteRepository siteRepository,
                       PageRepository pageRepository,
                       LemmaRepository lemmaRepository,
                       IndexRepository indexRepository,
                       ParseProperties properties) {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;
        this.properties = properties;
        page = properties.getPage();
        domainName = properties.getPage().getSite().getUrl();
        lemmasRank = new HashMap<>();
        lemmaPositionInText = new HashMap<>();
        currentLinks = new ArrayList<>();
    }

    @Override
    protected void compute() {
        if (ParseData.isInterrupted()) {
            Thread.currentThread().interrupt();
            return;
        }
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Logger.getLogger(LinksParser.class.getName()).info("Прерван анализ страницы " + page.getPath());
        }
        Logger.getLogger(LinksParser.class.getName())
                .info("ссылок найдено: " + ParseData.getFoundLinks().size() + " / поток №: " +
                        Thread.currentThread().getId() + " / просмотр: " + page.getPath() + " = " + page.getSite().getId());
        List<LinksParser> taskList = new ArrayList<>();
        ParseData.addFoundLink(page.getPath());
        try {
            parsePage();
            currentLinks.forEach(link -> {
                Page newPage = new Page();
                newPage.setSite(page.getSite());
                newPage.setPath(link);
                properties.setPage(newPage);
                LinksParser task = new LinksParser(siteRepository, pageRepository, lemmaRepository, indexRepository, properties);
                task.fork();
                taskList.add(task);
            });
            taskList.forEach(ForkJoinTask::join);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parsePage() throws IOException {
        currentLinks = jsoupLinksParser();
        insertDataToDB();
    }

    private List<String> jsoupLinksParser() throws IOException {
        List<String> links = new ArrayList<>();
        try {
            Connection.Response response = Jsoup.connect(page.getPath()).userAgent(properties.getUserAgent()).referrer("http://www.google.com").execute();
            int responseCode = response.statusCode();
            Document doc = response.parse();
            page.setContent(doc.toString());
            page.setCode(responseCode);
            getLemmasFromString(doc);
            Elements elements = doc.select("a[href]");
            elements.stream()
                    .map(element -> urlFormatter(element.attr("href")))
                    .filter(link -> link.startsWith(domainName))
                    .filter(link -> !ParseData.getFoundLinks().contains(link))
                    .filter(this::linkValidation)
                    .peek(links::add)
                    .forEach(ParseData::addFoundLink);
        }
        catch (HttpStatusException e) {
            page.setCode(e.getStatusCode());
            Logger.getLogger(LinksParser.class.getName()).info(e.getLocalizedMessage());
        }
        catch (ConnectException e) {
            page.setCode(500);
            Logger.getLogger(LinksParser.class.getName()).info(e.getLocalizedMessage());
        }
        return links;
    }

    private String urlFormatter(String url) {
        String correctUrl = url.isEmpty() ? "incorrect!" : url;
        try {
            correctUrl = correctUrl.charAt(0) == '/' ? domainName + correctUrl : correctUrl;
            correctUrl = correctUrl.charAt(correctUrl.length() - 1) == '/' ? correctUrl.substring(0, correctUrl.length() - 1) : correctUrl;
        }catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return correctUrl;
    }

    private String urlFormatterForDB(String url) {
        String urlForDB = url.substring(domainName.length());
        return urlForDB.isEmpty() ? urlForDB.concat("/") : urlForDB;
    }

    private boolean linkValidation(String link) {
        return !link.matches("(.+\\.(?!html)\\w{2,5}(/?\\?\\S+)?$)|(\\S+#\\S+)");
    }

    private void getLemmasFromString(Document text) {
        int lastFieldsWordsCount = 0;
        for (Field field : properties.getFields()) {
            String fieldText = text.getElementsByTag(field.getSelector()).text();
            int currentFieldLength = 0;
            Map<String, LemmaValues> lemmas = new LemmaCollector().getLemmas(fieldText);
            for (Map.Entry<String, LemmaValues> entry : lemmas.entrySet()) {
                Float count = lemmasRank.getOrDefault(entry.getKey(), 0f);
                lemmasRank.put(entry.getKey(), count + entry.getValue().getCount() * field.getWeight());
                currentFieldLength = entry.getValue().getTextLength();
                List<Integer> lemmaPositionInField = entry.getValue().getWordNumbers();
                int finalLastFieldsWordsCount = lastFieldsWordsCount;
                String findingLemmaPositions = lemmaPositionInText.getOrDefault(entry.getKey(), "");
                StringJoiner positionsInFieldStr = new StringJoiner(" ");
                if (!findingLemmaPositions.isEmpty()) {
                    positionsInFieldStr.add(findingLemmaPositions);
                }
                lemmaPositionInField.stream().map(i -> i += finalLastFieldsWordsCount).forEach(i -> positionsInFieldStr.add(String.valueOf(i)));
                lemmaPositionInText.put(entry.getKey(), positionsInFieldStr.toString());
            }
            lastFieldsWordsCount += currentFieldLength;
        }
    }

    private void insertDataToDB() {
        page.setPath(urlFormatterForDB(page.getPath()));
        insertPreparing();
        pageRepository.save(page);
        siteRepository.updateIndexingDate(page.getSite().getId());
        lemmasRank.keySet().forEach(l -> lemmaRepository.addLemma(l, page.getSite().getId()));
        List<Index> indexList = new ArrayList<>();
        lemmasRank.forEach((l, r) -> {
            Index index = new Index();
            index.setPage(pageRepository.findByPath(page.getPath(), page.getSite().getId()));
            index.setLemma(lemmaRepository.findByLemma(l, page.getSite().getId()));
            index.setRank(r);
            index.setIndexesInText(lemmaPositionInText.get(l));
            indexList.add(index);
        });
        indexRepository.saveAll(indexList);
    }

    private void insertPreparing() {
        Page existingPage = pageRepository.findByPath(page.getPath(), page.getSite().getId());
        if (existingPage != null) {
            existingPage.getLemmaList().forEach(lemmaRepository::decreaseFrequency);
            pageRepository.deleteById(existingPage.getId());
        }

    }
}
