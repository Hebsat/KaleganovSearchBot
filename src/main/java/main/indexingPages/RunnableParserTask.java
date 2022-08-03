package main.indexingPages;

import main.model.Site;
import main.properties.ParseProperties;
import main.repository.Repositories;

import java.util.concurrent.ForkJoinPool;

public class RunnableParserTask implements Runnable{

    private final Repositories repositories;
    private final ParseProperties properties;
    private final int threads;

    public RunnableParserTask(Repositories repositories, ParseProperties properties, int threads) {
        this.repositories = repositories;
        this.properties = properties;
        this.threads = threads;
    }

    @Override
    public void run() {
        System.out.println("start " + properties.getPage().getSite().getId());
        new ForkJoinPool(threads)
                .invoke(new LinksParser(repositories, properties));
        System.out.println("finish " + properties.getPage().getSite().getId());
        Site indexedSite = repositories.getSiteRepository().findIndexedSiteByUrl(properties.getPage().getSite().getUrl());
        if (indexedSite != null) {
            repositories.getSiteRepository().delete(indexedSite);
        }
        Site failedSite = repositories.getSiteRepository().findFailedSiteByUrl(properties.getPage().getSite().getUrl());
        if (failedSite != null) {
            repositories.getSiteRepository().delete(failedSite);
        }
        repositories.getSiteRepository().finishIndexingSite(properties.getPage().getSite().getId());
        System.out.println("finished " + properties.getPage().getSite().getId());
    }
}
