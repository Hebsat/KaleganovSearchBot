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

@Service
public class SearchService {

    @Autowired
    private Repositories repositories;

    @Autowired
    private SearchBotProperties searchBotProperties;

    public ResponseSearchObject search(String query, Site site) {
        System.out.println(query);
        RequestHandler requestHandler = new RequestHandler(repositories);
        List<ResponseObject> q = requestHandler.requestHandler(query, site);
        List<SearchData> qwe = new ArrayList<>();
        q.forEach(w -> System.out.println(site.getUrl() + w.getUri()));
        q.forEach(responseObject -> qwe.add(new SearchData(
                    site.getUrl(),
                    site.getName(),
                    responseObject.getUri(),
                    responseObject.getTitle(),
                    responseObject.getSnippet(),
                    responseObject.getRelevance())));
        return new ResponseSearchObject(true, q.size(), qwe.toArray(new SearchData[0]));
    }

    public Site getSite(String url) {
        return repositories.getSiteRepository().findIndexedSiteByUrl(url);
    }

    public boolean queryValidation(String query) {
        return query.matches("[а-яА-ЯёЁ\\s]+");
    }
}
