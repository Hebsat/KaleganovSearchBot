package main.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResponseSearchObject {

    private boolean result;
    private int count;
    private SearchData[] data;
}
