package main.indexingPages;

import main.lemmatization.LemmaCollector;
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
import java.util.*;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinksParser extends RecursiveAction {

    private final Repositories repositories;

    private final String domainName;
    private final Page page;
    private final Map<String, Float> lemmasRank;
    private final ParseProperties properties;

    public LinksParser(Repositories repositories, ParseProperties properties) {
        this.repositories = repositories;
        this.properties = properties;
        page = properties.getPage();
        domainName = properties.getPage().getSite().getUrl();
        lemmasRank = new HashMap<>();
    }

    @Override
    protected void compute() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("ссылок найдено: " + FoundLinks.getFoundLinks().size() + " / поток №: " + Thread.currentThread().getId() + " / просмотр: " + page.getPath() + " = " + page.getSite().getId());
        List<LinksParser> taskList = new ArrayList<>();
        FoundLinks.addFoundLink(page.getPath());
        try {
            List<String> currentLinks = jsoupLinksParser();
            currentLinks.forEach(link -> {
                Page newPage = new Page();
                newPage.setSite(page.getSite());
                newPage.setPath(link);
                properties.setPage(newPage);
                LinksParser task = new LinksParser(repositories, properties);
                task.fork();
                taskList.add(task);
            });
            insertDataToDB();
            taskList.forEach(ForkJoinTask::join);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> jsoupLinksParser() throws IOException {
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
                    .filter(link -> !FoundLinks.getFoundLinks().contains(link))
                    .filter(this::linkValidation)
                    .peek(links::add)
                    .forEach(FoundLinks::addFoundLink);
        }
        catch (HttpStatusException e) {
            page.setCode(e.getStatusCode());
            e.printStackTrace();
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
        return !link.matches("(" + domainName + "/\\S+\\.(?!html)\\w{2,5}$)|(\\S+#\\S+)");
    }

    private void getLemmasFromString(Document text) {
        for (Field field : properties.getFields()) {
            String fieldText = text.getElementsByTag(field.getSelector()).text();
            new LemmaCollector().getLemmas(fieldText).forEach((l, c) -> {
                Float count = lemmasRank.getOrDefault(l, 0f);
                lemmasRank.put(l, count + c * field.getWeight());
            });
        }
    }

    private void insertDataToDB() {
        page.setPath(urlFormatterForDB(page.getPath()));
        repositories.getPageRepository().save(page);
        repositories.getSiteRepository().updateIndexingDate(page.getSite().getId());
        lemmasRank.keySet().forEach(l ->repositories.getLemmaRepository().addLemma(l, page.getSite().getId()));
        lemmasRank.forEach((l, r) -> {
            Index index = new Index();
            index.setPage(repositories.getPageRepository().findByPath(page.getPath()));
            index.setLemma(repositories.getLemmaRepository().findByLemma(l, page.getSite().getId()));
            index.setRank(r);
            repositories.getIndexRepository().save(index);
        });
//        StringBuilder query = new StringBuilder();
//        lemmasRank.forEach((l, r) -> query.append(query.length() == 0 ? "" : "), ('").append(l).append("', '1'"));
//        System.out.println(query);
//        repositories.getLemmaRepository().addAllLemmas(query.substring(0, query.length() - 1));
    }
}
