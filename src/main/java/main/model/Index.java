package main.model;

import javax.persistence.*;

@Entity
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

    @Override
    public String toString() {
        return "Index{" +
                "id=" + id +
                " ".repeat(7 - String.valueOf(id).length()) + "page=" + page +
                ", lemma=" + lemma +
                ", rank=" + rank +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Lemma getLemma() {
        return lemma;
    }

    public void setLemma(Lemma lemma) {
        this.lemma = lemma;
    }

    public float getRank() {
        return rank;
    }

    public void setRank(float rank) {
        this.rank = rank;
    }
}
