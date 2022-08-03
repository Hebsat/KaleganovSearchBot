package main.controllers;

import main.response.ResponseErrorObject;
import main.services.IndexingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class IndexingController {

    @Autowired
    private IndexingService indexingService;

    @GetMapping("/api/startIndexing")
    public ResponseEntity<?> startIndexing() {
        if (indexingService.isIndexing()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseErrorObject(false, "Индексация уже запущена"));
        }
        indexingService.startIndexingAll();
        Map<String, Boolean> response = new HashMap<>();
        response.put("result", true);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/stopIndexing")
    public ResponseEntity<?> stopIndexing() {
        if (indexingService.isIndexing()) {
            indexingService.stopIndexing();
            Map<String, Boolean> result = new HashMap<>();
            result.put("result", true);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseErrorObject(false, "Индексация не запущена"));
    }

    @PostMapping("/api/indexPage")
    public ResponseEntity<?> indexPage(@RequestParam String url) {
        if (indexingService.indexPageValidation(url)) {
            indexingService.startIndexingSite(indexingService.getSiteParamsFromUrl(url));
            Map<String, Boolean> result = new HashMap<>();
            result.put("result", true);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseErrorObject(false,
                        "Данный сайт находится вне списка сайтов, указанных в конфигурационном файле"));
    }

    @GetMapping("/api/statistics")
    public ResponseEntity<?> getStatistics() {
        return ResponseEntity.status(HttpStatus.OK).body(indexingService.getStatistics());
    }
}
