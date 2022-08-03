package main.response;

import main.model.Status;

public class DetailedStatistics {

    private String url;
    private String name;
    private Status status;
    private long StatusTime;
    private String error;
    private long pages;
    private long lemmas;

    public DetailedStatistics(String url, String name, Status status, long statusTime, String error, long pages, long lemmas) {
        this.url = url;
        this.name = name;
        this.status = status;
        StatusTime = statusTime;
        this.error = error;
        this.pages = pages;
        this.lemmas = lemmas;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getStatusTime() {
        return StatusTime;
    }

    public void setStatusTime(long statusTime) {
        StatusTime = statusTime;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

    public long getLemmas() {
        return lemmas;
    }

    public void setLemmas(long lemmas) {
        this.lemmas = lemmas;
    }
}
