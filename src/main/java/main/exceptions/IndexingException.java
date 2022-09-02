package main.exceptions;

public class IndexingException extends Exception{

    private String message;

    public IndexingException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
