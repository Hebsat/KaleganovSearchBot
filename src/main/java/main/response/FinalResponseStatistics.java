package main.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FinalResponseStatistics {

    private boolean result;
    private ResponseStatistics statistics;
}
