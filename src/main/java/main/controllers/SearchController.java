package main.controllers;

import main.model.Site;
import main.response.ResponseErrorObject;
import main.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/api/search")
    public ResponseEntity<?> searchPages(@RequestParam String query,
                                      @RequestParam String site,
                                      @RequestParam(required = false) int offset,
                                      @RequestParam(required = false, defaultValue = "20") int limit) {
//        model.addAttribute("viewLimit", limit);
        System.out.println("<<<<<<query>>>>>>>" + query + site);
        if (query.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseErrorObject(false, "Задан пустой поисковый запрос"));
        }
        if (!searchService.queryValidation(query)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseErrorObject(false, "Задан некорректный поисковый запрос: " + query));
        }
        if (site.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseErrorObject(false, "Не указан сайт для поиска"));
        }
        Site currentSite = searchService.getSite(site);
        if (currentSite == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseErrorObject(false, "Указанный сайт не проиндексирован"));
        }
        System.out.println(currentSite.getId() + " - " + currentSite.getStatus() + " - " + currentSite.getUrl());
        return ResponseEntity.status(HttpStatus.OK).body(searchService.search(query, currentSite));
    }
}
