package main.lemmatization;

import lombok.AllArgsConstructor;
import main.indexingPages.ParseData;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

@AllArgsConstructor
public class Lemmatizer implements Callable<Word> {

    private final Word word;

    @Override
    public Word call() throws IOException {
        if (ParseData.isInterrupted() && !ParseData.isSearching()) {
            word.setLemmas(new ArrayList<>());
            return word;
        }
        LuceneMorphology luceneMorphology = new RussianLuceneMorphology();
        word.setLemmas(luceneMorphology.getMorphInfo(word.getWord()).stream()
                .map(this::partsOfSpeechFilter)
                .filter(Objects::nonNull)
                .toList());
        return word;
    }

    private String partsOfSpeechFilter(String word) {
        String[] invalid = {"ЧАСТ", "СОЮЗ", "МЕЖД", "ПРЕДЛ"};
        String[] elements = word.split(" ");
        return Arrays.stream(invalid).noneMatch(p -> p.equals(elements[1])) ?
                elements[0].substring(0, elements[0].indexOf('|')) : null;
    }
}
