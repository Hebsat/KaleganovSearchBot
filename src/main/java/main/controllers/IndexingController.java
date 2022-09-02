package main.controllers;

import main.exceptions.ErrorMessages;
import main.exceptions.IndexingException;
import main.response.FinalResponseStatistics;
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
    public ResponseEntity<?> startIndexing() throws IndexingException {
        if (indexingService.isIndexing()) {
            throw new IndexingException(ErrorMessages.INDEXING_STARTED_YET);
        }
        indexingService.startIndexingAll();
        Map<String, Boolean> response = new HashMap<>();
        response.put("result", true);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<?> stopIndexing() throws IndexingException {
        if (indexingService.isIndexing()) {
            indexingService.stopIndexing();
            Map<String, Boolean> result = new HashMap<>();
            result.put("result", true);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
        throw new IndexingException(ErrorMessages.INDEXING_NOT_STARTED);
    }

    @PostMapping("/indexPage")
    public ResponseEntity<?> indexSingleSite(@RequestParam String url) throws IndexingException {
        if (indexingService.isIndexing()) {
            throw new IndexingException(ErrorMessages.INDEXING_STARTED_YET);
        }
        if (indexingService.indexSiteValidation(url)) {
            indexingService.startIndexingSingleSite(url);
            Map<String, Boolean> result = new HashMap<>();
            result.put("result", true);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
        throw new IndexingException(ErrorMessages.SITE_OUT_OF_RANGE + url);
    }

    @PostMapping("/indexPage1")
    public ResponseEntity<?> indexSinglePage(@RequestParam String url) throws IOException, IndexingException {
        if (indexingService.isIndexing()) {
            throw new IndexingException(ErrorMessages.INDEXING_STARTED_YET);
        }
        if (indexingService.indexPageValidation(url)) {
            indexingService.startIndexingSinglePage(url);
            Map<String, Boolean> result = new HashMap<>();
            result.put("result", true);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
        throw new IndexingException(ErrorMessages.PAGE_OUT_OF_RANGE + url);
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new FinalResponseStatistics(true, indexingService.getStatistics()));
    }
}
