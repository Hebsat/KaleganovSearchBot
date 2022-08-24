package main.model;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "fields")
public class Field {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String selector;

    @Column(nullable = false)
    private float weight;

}
