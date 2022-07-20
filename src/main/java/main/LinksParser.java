package main;

import main.model.Page;
import main.model.PageRepository;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinksParser extends RecursiveAction {

    private final String url;
    private final String domainName;
    String regexUrl = "(https?:\\/\\/)(www\\.)?([\\w-]+)\\.([a-z]{2,6})";

    private Page page = new Page();

    private PageRepository pageRepository;


    public LinksParser(String url, PageRepository pageRepository) {
        this.url = urlFormatter(url);
        this.domainName = getDomainNameFromLink(url);
        page.setPath(urlFormatterForDB(url));
        this.pageRepository = pageRepository;
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
                LinksParser task = new LinksParser(link, pageRepository);
                task.fork();
                taskList.add(task);
            });
            taskList.forEach(ForkJoinTask::join);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> jsoupLinksParser() throws IOException {
        List<String> links = new ArrayList<>();
        Connection.Response response = Jsoup.connect(url).userAgent("KaleganovSearchBot-1.0").referrer("http://www.google.com").execute();
        int responseCode = response.statusCode();
        Document doc = response.parse();
        page.setContent(doc.toString());
        page.setCode(responseCode);
        pageRepository.save(page);
        Elements elements = doc.select("a[href]");
        elements.stream()
                .map(element -> urlFormatter(element.attr("href")))
                .filter(link -> link.startsWith(domainName))
                .filter(link -> !FoundLinks.getFoundLinks().contains(link))
                .filter(this::linkValidation)
                .peek(links::add)
                .forEach(FoundLinks::addFoundLink);
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
        return !link.matches("(" + domainName + "\\/\\S+\\.(?!html)\\w{2,5}$)|(\\S+#\\S+)");
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
}
