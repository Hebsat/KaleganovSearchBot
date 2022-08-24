package main.controllers;

import main.response.FinalResponseStatistics;
import main.response.ResponseErrorObject;
import main.services.IndexingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class IndexingController {

    @Autowired
    private IndexingService indexingService;

    @GetMapping("/startIndexing")
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

    @GetMapping("/stopIndexing")
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

    @PostMapping("/indexPage")
    public ResponseEntity<?> indexSingleSite(@RequestParam String url) {
        if (indexingService.isIndexing()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseErrorObject(false, "Индексация уже запущена"));
        }
        if (indexingService.indexSiteValidation(url)) {
            indexingService.startIndexingSingleSite(url);
            Map<String, Boolean> result = new HashMap<>();
            result.put("result", true);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseErrorObject(false,
                        "Данный сайт находится вне списка сайтов, указанных в конфигурационном файле: " + url));
    }

    @PostMapping("/indexPage1")
    public ResponseEntity<?> indexSinglePage(@RequestParam String url) throws IOException {
        if (indexingService.isIndexing()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseErrorObject(false, "Индексация уже запущена"));
        }
        if (indexingService.indexPageValidation(url)) {
            indexingService.startIndexingSinglePage(url);
            Map<String, Boolean> result = new HashMap<>();
            result.put("result", true);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseErrorObject(false,
                        "Данная страница находится вне списка сайтов, указанных в конфигурационном файле: " + url));
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new FinalResponseStatistics(true, indexingService.getStatistics()));
    }
}
