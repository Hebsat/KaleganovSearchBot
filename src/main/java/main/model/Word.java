package main.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Word {

    private String word;
    private List<String> lemmas;
    private int number;

    public Word(String word) {
        this.word = word;
    }
}
