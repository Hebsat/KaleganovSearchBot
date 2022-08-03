package main.model;

import javax.persistence.*;
import java.util.Date;

@Entity
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(Date statusTime) {
        this.statusTime = statusTime;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
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
}
