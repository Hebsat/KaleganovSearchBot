package main.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Index;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
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
    @ManyToOne ()
    private Site site;

    @ManyToMany()
    @JoinTable(name = "indexes",
            joinColumns = {@JoinColumn (name = "page_id")},
            inverseJoinColumns = {@JoinColumn (name = "lemma_id")})
    private List<Lemma> lemmaList;

    @Transient
    private float relevance;

    @Transient
    private Set<Integer> lemmasPositions;

    public Page(int id, String path, int code, String content, Site site, float relevance, Set<Integer> lemmasPositions) {
        this.id = id;
        this.path = path;
        this.code = code;
        this.content = content;
        this.site = site;
        this.relevance = relevance;
        this.lemmasPositions = lemmasPositions;
    }

    @Override
    public String toString() {
        return "Page{" +
                "id=" + id +
                " ".repeat(6 - String.valueOf(id).length()) + "path='" + (path.length() > 28 ? path.substring(0, 28) : path) + '\'' +
                " ".repeat(30 - (path.length() > 28 ? path.substring(0, 28).length() : path.length())) + "code=" + code +
                " relevance: " + getRelevance() +
                '}';
    }
}
