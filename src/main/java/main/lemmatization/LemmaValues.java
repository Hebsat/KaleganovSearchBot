package main.lemmatization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class LemmaValues {

    private String lemma;
    private int count;
    private List<Integer> wordNumbers;
    private int textLength;
}
