package main.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "sites")
public class Site {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, columnDefinition = "enum")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "status_time ", nullable = false)
    private Date statusTime;

    @Column(name = "last_error", columnDefinition = "text")
    private String lastError;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String name;
}
