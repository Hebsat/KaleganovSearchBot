package main.exceptions;

public class SearchException extends Exception{

    private String message;

    public SearchException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
