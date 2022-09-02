package main.controllers;

import main.exceptions.IndexingException;
import main.exceptions.SearchException;
import main.response.ResponseErrorObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorController {

    @ExceptionHandler(IndexingException.class)
    public ResponseEntity<?> handleIndexingException(IndexingException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseErrorObject(false, exception.getMessage()));
    }

    @ExceptionHandler(SearchException.class)
    public ResponseEntity<?> handleSearchException(SearchException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseErrorObject(false, exception.getMessage()));
    }
}
