package main.model;

import javax.persistence.*;
import javax.persistence.Index;

@Entity
@Table(name = "pages", indexes = @Index(name = "path_index", columnList = "path"))
public class Page {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private int code;

    @Column(nullable = false, columnDefinition = "mediumtext")
    private String content = "";

    @JoinColumn(name = "site_id", updatable = false, nullable = false)
    @ManyToOne (cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, optional = false)
    private Site site;

    @Transient
    private float relevance;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public float getRelevance() {
        return relevance;
    }

    public void setRelevance(float relevance) {
        this.relevance = relevance;
    }

    @Override
    public String toString() {
        return "Page{" +
                "id=" + id +
                " ".repeat(6 - String.valueOf(id).length()) + "path='" + (path.length() > 28 ? path.substring(0, 28) : path) + '\'' +
                " ".repeat(30 - (path.length() > 28 ? path.substring(0, 28).length() : path.length())) + "code=" + code +
                '}';
    }
}
