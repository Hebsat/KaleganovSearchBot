package main.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "lemmas", indexes = @javax.persistence.Index(name = "lemma_site_index", columnList = "lemma, site_id", unique = true))
public class Lemma {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String lemma;

    @Column(nullable = false)
    private int frequency;

    @JoinColumn(name = "site_id", updatable = false, nullable = false)
    @ManyToOne (cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, optional = false)
    private Site site;

    @OneToMany (fetch = FetchType.EAGER, mappedBy = "lemma", orphanRemoval = true)
    List<Index> indexes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public List<Index> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<Index> indexes) {
        this.indexes = indexes;
    }

    @Override
    public String toString() {
        return "Lemma{" +
                "id=" + id +
                " ".repeat(7 - String.valueOf(id).length()) + "lemma='" + lemma + '\'' +
                " ".repeat(20 - lemma.length()) + "frequency=" + frequency +
//                ", indexes=" + indexes +
                '}';
    }
}
