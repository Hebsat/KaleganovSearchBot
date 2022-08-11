package main.lemmatization;

import java.util.List;

public class LemmaValues {

    private String lemma;
    private int count;
    private List<Integer> wordNumbers;
    private int textLength;

    public LemmaValues(String lemma, int count, List<Integer> wordNumbers, int textLength) {
        this.lemma = lemma;
        this.count = count;
        this.wordNumbers = wordNumbers;
        this.textLength = textLength;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Integer> getWordNumbers() {
        return wordNumbers;
    }

    public void setWordNumbers(List<Integer> wordNumbers) {
        this.wordNumbers = wordNumbers;
    }

    public int getTextLength() {
        return textLength;
    }

    public void setTextLength(int textLength) {
        this.textLength = textLength;
    }
}
