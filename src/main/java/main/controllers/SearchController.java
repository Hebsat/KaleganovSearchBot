package main.controllers;

import main.exceptions.ErrorMessages;
import main.exceptions.SearchException;
import main.model.Site;
import main.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/admin")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/search")
    public ResponseEntity<?> searchPages(@RequestParam String query,
                                      @RequestParam String site,
                                      @RequestParam(required = false) int offset,
                                      @RequestParam(required = false, defaultValue = "20") int limit) throws SearchException {
        if (query.isBlank()) {
            throw new SearchException(ErrorMessages.EMPTY_QUERY);
        }
        if (!searchService.queryValidation(query)){
            throw new SearchException(ErrorMessages.INCORRECT_QUERY + query);
        }
        if (site.isBlank()) {
            throw new SearchException(ErrorMessages.EMPTY_SITE);
        }
        Site currentSite = searchService.getSite(site);
        if (currentSite == null) {
            throw new SearchException(ErrorMessages.UNINDEXED_SITE);
        }
        Logger.getLogger(SearchController.class.getName())
                .info("Запрос: " + query + " / на сайте " + currentSite.getUrl() +
                        " offset: " + offset + " limit: " + limit);
        return ResponseEntity.status(HttpStatus.OK).body(searchService.search(query, currentSite, offset, limit));
    }
}
