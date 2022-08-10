package main.services;

import main.findingSystem.RequestHandler;
import main.findingSystem.ResponseObject;
import main.model.Site;
import main.properties.SearchBotProperties;
import main.repository.Repositories;
import main.response.ResponseSearchObject;
import main.response.SearchData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class SearchService {

    @Autowired
    private Repositories repositories;

    @Autowired
    private SearchBotProperties searchBotProperties;

    public ResponseSearchObject search(String query, Site site, int offset, int limit) {
        RequestHandler requestHandler = new RequestHandler(repositories);
        List<ResponseObject> foundPages = requestHandler.requestHandler(query, site);
        List<ResponseObject> resultFoundPages = getPagesInRange(foundPages, offset, limit);
        List<SearchData> searchDataList = new ArrayList<>();
        resultFoundPages.forEach(w -> Logger.getLogger(SearchService.class.getName()).info(site.getUrl() + w.getUri()));
        resultFoundPages.forEach(responseObject -> searchDataList.add(new SearchData(
                    site.getUrl(),
                    site.getName(),
                    responseObject.getUri(),
                    responseObject.getTitle(),
                    responseObject.getSnippet(),
                    responseObject.getRelevance())));
        return new ResponseSearchObject(true, foundPages.size(), searchDataList.toArray(new SearchData[0]));
    }

    public Site getSite(String url) {
        return repositories.getSiteRepository().findIndexedSiteByUrl(url);
    }

    public boolean queryValidation(String query) {
        return query.matches("[а-яА-ЯёЁ\\s]+");
    }

    private List<ResponseObject> getPagesInRange(List<ResponseObject> list, int offset, int limit) {
        List<ResponseObject> result = new ArrayList<>();
        if (list.size() < offset) {
            return new ArrayList<>();
        }
        for (int i = offset; i < list.size(); i++) {
            if (i == offset + limit) {
                break;
            }
            result.add(list.get(i));
        }
        return result;
    }
}
