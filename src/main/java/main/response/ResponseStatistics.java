package main.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResponseStatistics {

    private TotalStatistics total;
    private DetailedStatistics[] detailed;
}
