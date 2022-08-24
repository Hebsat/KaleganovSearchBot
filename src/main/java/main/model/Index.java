package main.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "indexes")
public class Index {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name = "page_id", updatable = false, nullable = false)
    @ManyToOne (cascade = CascadeType.REMOVE)
    private Page page;

    @JoinColumn(name = "lemma_id", updatable = false, nullable = false)
    @ManyToOne (cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, optional = false)
    private Lemma lemma;

    @Column(name = "`rank`", nullable = false)
    private float rank;

    @Column(name = "indexes_in_text")
    private String indexesInText;

    @Override
    public String toString() {
        return "Index{" +
                "id=" + id +
                " ".repeat(7 - String.valueOf(id).length()) + "page=" + page +
                ", lemma=" + lemma +
                ", rank=" + rank +
                '}';
    }
}
