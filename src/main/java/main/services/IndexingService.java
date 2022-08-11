package main.services;

import main.indexingPages.LinksParser;
import main.indexingPages.ParseData;
import main.indexingPages.RunnableParserTask;
import main.model.Field;
import main.model.Page;
import main.model.Site;
import main.model.Status;
import main.properties.ParseProperties;
import main.properties.SearchBotProperties;
import main.properties.SiteParams;
import main.repository.Repositories;
import main.response.DetailedStatistics;
import main.response.ResponseStatistics;
import main.response.TotalStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IndexingService {

    ExecutorService service;

    @Autowired
    private Repositories repositories;

    @Autowired
    private SearchBotProperties searchBotProperties;

    public void startIndexingSite(SiteParams link) {
        String url = getDomainNameFromLink(link.getUrl());
        String name = link.getName();
        repositories.getSiteRepository().startIndexingSite(url, name);
        Site site = repositories.getSiteRepository().findIndexingSiteByUrl(url);
        try {
            ParseProperties properties = new ParseProperties();
            properties.setUserAgent(searchBotProperties.getUserAgent());
            properties.setFields(new ArrayList<>((Collection<Field>) repositories.getFieldRepository().findAll()));
            properties.setForkJoinThreads(Runtime.getRuntime().availableProcessors() / searchBotProperties.getThreadNumber());
            Page page = new Page();
            page.setSite(site);
            page.setPath(site.getUrl());
            properties.setPage(page);

            service.submit(new RunnableParserTask(repositories, properties));
        }
        catch (Exception e) {
            repositories.getSiteRepository().failedIndexingSite(site.getId(), "Ошибка запуска индексации");
            e.printStackTrace();
        }
    }

    public void startIndexingSinglePage(String url) throws IOException {
        Logger.getLogger(IndexingService.class.getName()).info("Индексация отдельной страницы: " + url);
        String domainName = getDomainNameFromLink(url);
        Site site = repositories.getSiteRepository().findIndexedSiteByUrl(domainName);
        ParseProperties properties = new ParseProperties();
        properties.setUserAgent(searchBotProperties.getUserAgent());
        properties.setFields(new ArrayList<>((Collection<Field>) repositories.getFieldRepository().findAll()));
        Page page = new Page();
        page.setSite(site);
        page.setPath(url);
        properties.setPage(page);
        new LinksParser(repositories, properties).parsePage();
        Logger.getLogger(IndexingService.class.getName()).info("Индексация " + url + " завершена");
    }

    public void startIndexingAll() {
        prepareIndexing();
        service = Executors.newFixedThreadPool(searchBotProperties.getThreadNumber());
        for (SiteParams site : searchBotProperties.getLinks()) {
            startIndexingSite(site);
        }
    }

    public void startIndexingSingleSite(String url) {
        prepareIndexing();
        service = Executors.newSingleThreadExecutor();
        startIndexingSite(getSiteParamsFromUrl(url));
    }

    private void prepareIndexing() {
        ParseData.setInterrupted(false);
        if (service != null && !service.isShutdown()) {
            service.shutdown();
        }
        ParseData.clearFoundLinks();
    }

    public boolean isIndexing() {
        List<Site> sites = new ArrayList<>();
        repositories.getSiteRepository().findAll().forEach(sites::add);
        return sites.stream().anyMatch(site -> site.getStatus() == Status.INDEXING);
    }

    public void stopIndexing() {
        ParseData.setInterrupted(true);
        Logger.getLogger(IndexingService.class.getName()).info("set interrupted " + ParseData.isInterrupted());
    }

    public boolean indexSiteValidation(String url) {
        SiteParams[] links = searchBotProperties.getLinks();
        return Arrays.stream(links)
                .anyMatch(link -> getDomainNameFromLink(link.getUrl()).equals(url));
    }

    public boolean indexPageValidation(String url) {
        SiteParams[] links = searchBotProperties.getLinks();
        return Arrays.stream(links)
                .anyMatch(link -> getDomainNameFromLink(link.getUrl()).equals(getDomainNameFromLink(url)));
    }

    public SiteParams getSiteParamsFromUrl(String url) {
        SiteParams[] links = searchBotProperties.getLinks();
        return Arrays.stream(links).filter(l -> getDomainNameFromLink(l.getUrl()).equals(url)).findFirst().get();
    }

    private String getDomainNameFromLink(String link) {
        String regexUrl = "(https?://)(www\\.)?([\\w-]+)\\.([a-z]{2,6})";
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

    public ResponseStatistics getStatistics() {
        List<DetailedStatistics> detailedStatistics = new ArrayList<>();
        List<Site> sites = new ArrayList<>();
        repositories.getSiteRepository().findAll().forEach(sites::add);
        sites.forEach(site -> detailedStatistics.add(new DetailedStatistics(
                site.getUrl(),
                site.getName(),
                site.getStatus(),
                site.getStatusTime().getTime(),
                site.getLastError() == null ? "Ошибки отсутствуют" : site.getLastError(),
                repositories.getPageRepository().findCountBySiteId(site.getId()),
                repositories.getLemmaRepository().findCountBySiteId(site.getId())
        )));
        return new ResponseStatistics(
                new TotalStatistics(
                        repositories.getSiteRepository().count(),
                        repositories.getPageRepository().count(),
                        repositories.getLemmaRepository().count(),
                        isIndexing()),
                detailedStatistics.toArray(new DetailedStatistics[0])
        );
    }
}
