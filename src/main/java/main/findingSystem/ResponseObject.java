package main.findingSystem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseObject {

    private String uri;
    private String title;
    private String snippet;
    private float relevance;
}