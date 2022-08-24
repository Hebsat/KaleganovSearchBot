package main.indexingPages;

import lombok.AllArgsConstructor;
import main.model.Site;
import main.properties.ParseProperties;
import main.repository.*;

import java.util.concurrent.ForkJoinPool;
import java.util.logging.Logger;

@AllArgsConstructor
public class RunnableParserTask implements Runnable{

    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final ParseProperties properties;

    @Override
    public void run() {
        Logger.getLogger(RunnableParserTask.class.getName())
                .info("start " + properties.getPage().getSite().getId() + " - " + properties.getPage().getPath() + " in " + properties.getForkJoinThreads() + " threads");
        new ForkJoinPool(properties.getForkJoinThreads())
                .invoke(new LinksParser(siteRepository, pageRepository, lemmaRepository, indexRepository, properties));
        Logger.getLogger(RunnableParserTask.class.getName())
                .info("finishing " + properties.getPage().getSite().getId() + " - " + properties.getPage().getPath());
        Site indexedSite = siteRepository.findIndexedSiteByUrl(properties.getPage().getSite().getUrl());
        if (indexedSite != null && !ParseData.isInterrupted()) {
            deleteData(indexedSite);
        }
        Site failedSite = siteRepository.findFailedSiteByUrl(properties.getPage().getSite().getUrl());
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
        pageRepository.deleteBySiteId(site.getId());
        lemmaRepository.deleteBySiteId(site.getId());
        siteRepository.delete(site);
    }

    private void setIndexed() {
        siteRepository.finishIndexingSite(properties.getPage().getSite().getId());
    }

    private void setFailed() {
        siteRepository.stopIndexingSites(properties.getPage().getSite().getId(), "Индексация прервана");
    }
}
