package main.indexingPages;

import main.model.Site;
import main.properties.ParseProperties;
import main.repository.Repositories;

import java.util.concurrent.ForkJoinPool;
import java.util.logging.Logger;

public class RunnableParserTask implements Runnable{

    private final Repositories repositories;
    private final ParseProperties properties;

    public RunnableParserTask(Repositories repositories, ParseProperties properties) {
        this.repositories = repositories;
        this.properties = properties;
    }

    @Override
    public void run() {
        Logger.getLogger(RunnableParserTask.class.getName())
                .info("start " + properties.getPage().getSite().getId() + " - " + properties.getPage().getPath() + " in " + properties.getForkJoinThreads() + " threads");
        new ForkJoinPool(properties.getForkJoinThreads())
                .invoke(new LinksParser(repositories, properties));
        Logger.getLogger(RunnableParserTask.class.getName())
                .info("finishing " + properties.getPage().getSite().getId() + " - " + properties.getPage().getPath());
        Site indexedSite = repositories.getSiteRepository().findIndexedSiteByUrl(properties.getPage().getSite().getUrl());
        if (indexedSite != null && !ParseData.isInterrupted()) {
            deleteData(indexedSite);
        }
        Site failedSite = repositories.getSiteRepository().findFailedSiteByUrl(properties.getPage().getSite().getUrl());
        if (failedSite != null) {
            deleteData(failedSite);
        }
        if (ParseData.isInterrupted()) {
            setFailed();
        } else {
            setIndexed();
        }
        Logger.getLogger(RunnableParserTask.class.getName())
                .info("finished " + properties.getPage().getSite().getId() + " - " + properties.getPage().getPath());
    }

    private void deleteData(Site site) {
        repositories.getPageRepository().deleteBySiteId(site.getId());
        repositories.getLemmaRepository().deleteBySiteId(site.getId());
        repositories.getSiteRepository().delete(site);
    }

    private void setIndexed() {
        repositories.getSiteRepository().finishIndexingSite(properties.getPage().getSite().getId());
    }

    private void setFailed() {
        repositories.getSiteRepository().stopIndexingSites(properties.getPage().getSite().getId(), "Индексация прервана");
    }
}
