package main.properties;

import lombok.Getter;
import lombok.Setter;
import main.model.Field;
import main.model.Page;

import java.util.List;

@Getter
@Setter
public class ParseProperties {

    private String userAgent;
    private List<Field> fields;
    private Page page;
    private int forkJoinThreads;
}
