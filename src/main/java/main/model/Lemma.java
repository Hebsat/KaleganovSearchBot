package main.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
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
    @ManyToOne (cascade = CascadeType.ALL)
    private Site site;

    @OneToMany (cascade = CascadeType.ALL, mappedBy = "lemma")
    List<Index> indexes;

    @Override
    public String toString() {
        return "Lemma{" +
                "id=" + id +
                " ".repeat(7 - String.valueOf(id).length()) + "lemma='" + lemma + '\'' +
                " ".repeat(20 - lemma.length()) + "frequency=" + frequency +
                '}';
    }
}
