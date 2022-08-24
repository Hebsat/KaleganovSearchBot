package main.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import main.model.Status;

@Getter
@Setter
@AllArgsConstructor
public class DetailedStatistics {

    private String url;
    private String name;
    private Status status;
    private long statusTime;
    private String error;
    private long pages;
    private long lemmas;
}
