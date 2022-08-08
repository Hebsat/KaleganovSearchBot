package main.properties;

import main.model.Field;
import main.model.Page;

import java.util.List;

public class ParseProperties {

    private String userAgent;
    private List<Field> fields;
    private Page page;
    private int forkJoinThreads;

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public int getForkJoinThreads() {
        return forkJoinThreads;
    }

    public void setForkJoinThreads(int forkJoinThreads) {
        this.forkJoinThreads = forkJoinThreads;
    }
}
