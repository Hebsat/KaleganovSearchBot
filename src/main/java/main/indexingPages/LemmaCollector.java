package main.indexingPages;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class LemmaCollector {

    private Map<String, Integer> lemmasMap = new HashMap<>();

    ExecutorService service = Executors.newFixedThreadPool(2);
    List<FutureTask<List<String>>> tasks = new ArrayList<>();

    public Map<String, Integer> getLemmas(String text) {
        List<String> words = new ArrayList<>();
        getWordsFromText(text).stream()
                .map(this::wordFormatterToLowerCase)
                .filter(this::wordValidator)
                .forEach(w -> {
                    FutureTask<List<String>> futureTask = new FutureTask<>(new Lemmatizer(w));
                    tasks.add(futureTask);
//                    try {
//                        words.addAll(getLemmasFromWord(w));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                });
        tasks.forEach(t -> service.submit(t));
        tasks.forEach(t -> {
            try {
                words.addAll(t.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        words.forEach(w -> {
            int count = lemmasMap.getOrDefault(w, 0);
            lemmasMap.put(w, count + 1);
        });
        service.shutdown();
        return lemmasMap;
    }

    private List<String> getWordsFromText(String text) {
        return Arrays.stream(text.split("\\s+")).toList();
    }

    private String wordFormatterToLowerCase(String word) {
        return word.replaceAll("[.,!?'\"]+", "").toLowerCase();
    }

    private boolean wordValidator(String word) {
        return word.matches("[а-яё]+");
    }

//    private String partsOfSpeechFilter(String word) {
//        String[] invalid = {"ЧАСТ", "СОЮЗ", "МЕЖД", "ПРЕДЛ"};
//        String[] elements = word.split(" ");
//        return Arrays.stream(invalid).noneMatch(p -> p.equals(elements[1])) ?
//                elements[0].substring(0, elements[0].indexOf('|')) : null;
//    }
//
//    private List<String> getLemmasFromWord(String word) throws IOException {
//        LuceneMorphology luceneMorphology = new RussianLuceneMorphology();
//        return luceneMorphology.getMorphInfo(word).stream()
//                .map(this::partsOfSpeechFilter)
//                .filter(Objects::nonNull)
//                .toList();
//    }
}
