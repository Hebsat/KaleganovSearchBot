package main.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TotalStatistics {

    private long sites;
    private long pages;
    private long lemmas;
    private boolean isIndexing;
}
