package main.lemmatization;

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

public class Lemmatizer implements Callable<List<String>> {

    String word;

    public Lemmatizer(String word) {
        this.word = word;
    }

    @Override
    public List<String> call() throws IOException {
        if (ParseData.isInterrupted() && !ParseData.isSearching()) {
            return new ArrayList<>();
        }
        LuceneMorphology luceneMorphology = new RussianLuceneMorphology();
        return luceneMorphology.getMorphInfo(word).stream()
                .map(this::partsOfSpeechFilter)
                .filter(Objects::nonNull)
                .toList();
    }

    private String partsOfSpeechFilter(String word) {
        String[] invalid = {"ЧАСТ", "СОЮЗ", "МЕЖД", "ПРЕДЛ"};
        String[] elements = word.split(" ");
        return Arrays.stream(invalid).noneMatch(p -> p.equals(elements[1])) ?
                elements[0].substring(0, elements[0].indexOf('|')) : null;
    }
}
