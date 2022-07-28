package main.indexingPages;

import main.model.Field;
import main.model.Index;
import main.model.Page;
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

    private final String url;
    private final String domainName;
    String regexUrl = "(https?://)(www\\.)?([\\w-]+)\\.([a-z]{2,6})";

    private final Repositories repositories;


    private final Page page;
    private final Map<String, Float> lemmasRank;
    private final List<Field> fields;

    public LinksParser(String url, Repositories repositories, List<Field> fields) {
        this.url = urlFormatter(url);
        this.domainName = getDomainNameFromLink(url);
        this.repositories = repositories;
        this.fields = new ArrayList<>(fields);
        page = new Page();
        page.setPath(urlFormatterForDB(url));
        lemmasRank = new HashMap<>();
    }

    @Override
    protected void compute() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("ссылок найдено: " + FoundLinks.getFoundLinks().size() + " / поток №: " + Thread.currentThread().getId() + " / просмотр: " + url);
        List<LinksParser> taskList = new ArrayList<>();
        FoundLinks.addFoundLink(url);
        try {
            List<String> currentLinks = jsoupLinksParser();
            currentLinks.forEach(link -> {
                LinksParser task = new LinksParser(link, repositories, fields);
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
            Connection.Response response = Jsoup.connect(url).userAgent("KaleganovSearchBot-1.0").referrer("http://www.google.com").execute();
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

    private String getDomainNameFromLink(String link) {
        Pattern pattern = Pattern.compile(regexUrl);
        Matcher matcher = pattern.matcher(link);
        int start = 0;
        int end = 0;
        while (matcher.find()){
            start = matcher.start();
            end = matcher.end();
        }
        return link.substring(start, end);
    }

    private void getLemmasFromString(Document text) {
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            String fieldText = text.getElementsByTag(field.getSelector()).text();
            new LemmaCollector().getLemmas(fieldText).forEach((l, c) -> {
                Float count = lemmasRank.getOrDefault(l, 0f);
                lemmasRank.put(l, count + c * field.getWeight());
            });
        }
    }

    private void insertDataToDB() {
        repositories.getPageRepository().save(page);
        lemmasRank.keySet().forEach(l ->repositories.getLemmaRepository().addLemma(l));
        lemmasRank.forEach((l, r) -> {
            Index index = new Index();
            index.setPage(repositories.getPageRepository().findByPath(page.getPath()));
            index.setLemma(repositories.getLemmaRepository().findByLemma(l));
            index.setRank(r);
            repositories.getIndexRepository().save(index);
        });
//        StringBuilder query = new StringBuilder();
//        lemmasRank.forEach((l, r) -> query.append(query.length() == 0 ? "" : "), ('").append(l).append("', '1'"));
//        System.out.println(query);
//        repositories.getLemmaRepository().addAllLemmas(query.substring(0, query.length() - 1));
    }
}
