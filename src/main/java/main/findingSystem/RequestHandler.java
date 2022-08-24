package main.findingSystem;

import main.indexingPages.ParseData;
import main.lemmatization.LemmaCollector;
import main.model.Lemma;
import main.model.Page;
import main.model.Site;
import main.repository.LemmaRepository;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@Component
public class RequestHandler {

    private final LemmaRepository lemmaRepository;
    private final List<Lemma> requestLemmas;

    public RequestHandler(LemmaRepository lemmaRepository) {
        this.lemmaRepository = lemmaRepository;
        requestLemmas = new ArrayList<>();
    }

    public List<ResponseObject> requestHandler(String request, Site site) {
        List<ResponseObject> result = new ArrayList<>();
        ParseData.setSearching(true);
        new LemmaCollector().getLemmas(request).keySet().forEach(l -> requestLemmas.add(getLemma(l, site)));
        ParseData.setSearching(false);
        //Строка ниже позволяет осуществлять поиск при отсутствии некоторых лемм в БД
        requestLemmas.removeIf(Objects::isNull);
        if (requestLemmas.contains(null) | requestLemmas.isEmpty()) {
            return new ArrayList<>();
        }
        requestLemmas.sort(Comparator.comparing(Lemma::getFrequency));
        pageListCreator(requestLemmas).forEach(page -> {
            try {
                result.add(getResponseObject(page));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        result.sort(Comparator.comparing(ResponseObject::getRelevance));
        Collections.reverse(result);
        return result;
    }

    private Lemma getLemma(String lemma, Site site) {
        return lemmaRepository.findByLemma(lemma, site.getId());
    }

    private List<Page> getPages(Lemma lemma) {
        List<Page> pages = new ArrayList<>();
        lemma.getIndexes().forEach(i -> {
            Set<Integer> lemmasPositions = parsePositions(i.getIndexesInText());
            Page page = new Page(
                    i.getPage().getId(),
                    i.getPage().getPath(),
                    i.getPage().getCode(),
                    i.getPage().getContent(),
                    i.getPage().getSite(),
                    i.getRank(),
                    lemmasPositions);
            pages.add(page);
        });
        Logger.getLogger(RequestHandler.class.getName()).info(lemma.getLemma() + " - " + pages.size());
        return pages;
    }

    private Set<Integer> parsePositions(String text) {
        Set<Integer> lemmasPositions = new HashSet<>();
        Arrays.stream(text.split(" "))
                .map(Integer::valueOf)
                .forEach(lemmasPositions::add);
        return lemmasPositions;
    }

    private List<Page> pageListCreator(List<Lemma> lemmasList) {
        List<Page> pageList = new ArrayList<>();
        for (Lemma lemma : lemmasList) {
            List<Page> currentPages = getPages(lemma);
            if (pageList.isEmpty()) {
                pageList.addAll(currentPages);
                continue;
            }
            List<Page> finalPageList = new ArrayList<>();
            pageList.forEach(page1 -> currentPages.forEach(page2 -> {
                if (page2.getId() == page1.getId()) {
                    page1.setRelevance(page1.getRelevance() + page2.getRelevance());
                    Set<Integer> lemmasPositions = new HashSet<>();
                    lemmasPositions.addAll(page1.getLemmasPositions());
                    lemmasPositions.addAll(page2.getLemmasPositions());
                    page1.setLemmasPositions(lemmasPositions);
                    finalPageList.add(page1);
                }
            }));
            pageList = new ArrayList<>(finalPageList);
            if (pageList.isEmpty()) {
                break;
            }
        }
        return pageList;
    }

    private ResponseObject getResponseObject(Page page) throws IOException {
        ResponseObject responseObject = new ResponseObject();
        responseObject.setUri(page.getPath());
        responseObject.setTitle(getPageTitle(page));
        responseObject.setRelevance(page.getRelevance());
        responseObject.setSnippet(getSnippet(page));
        return responseObject;
    }

    private String getPageTitle(Page page) {
        return Jsoup.parse(page.getContent()).getElementsByTag("title").text();
    }

    private String getSnippet(Page page) {
        List<Integer> lemmaPositions = new ArrayList<>(page.getLemmasPositions());
        Collections.sort(lemmaPositions);
        String text = Jsoup.parse(page.getContent()).getAllElements().text();
        String[] words = text.split("\\s+");
        return parseSnippet(getSnippetRange(lemmaPositions), words);
    }

    private List<Integer> getSnippetRange(List<Integer> positions) {
        if (positions.size() < 2 || positions.get(positions.size() - 1) - positions.get(0) < 30) {
            return positions;
        }
        List<Integer> newRangeList = new ArrayList<>();
        int maxRange = 0;
        int startPosition = 0;
        int checkedRange = 0;
        int checkedPosition = 0;
        for (int i = checkedPosition; i < positions.size(); i++) {
            if (positions.get(checkedPosition) + 30 - positions.get(i) > 0) {
                checkedRange++;
                if (i != positions.size() - 1) {
                    continue;
                }
            }
            if (checkedRange > maxRange) {
                startPosition = checkedPosition;
                maxRange = checkedRange;
            }
            checkedRange = 0;
            checkedPosition++;
            i = checkedPosition;
        }
        for (int i = startPosition; i < startPosition + maxRange; i++) {
            newRangeList.add(positions.get(i));
        }
        return newRangeList;
    }

    private String parseSnippet(List<Integer> positions, String[] words) {
        StringJoiner snippet = new StringJoiner(" ");
        int start = positions.get(0) > 3 ? positions.get(0) - 4 : 0;
        int stop = words.length - positions.get(positions.size() - 1) < 5 ?
                words.length - 1 : positions.get(positions.size() - 1) + 5;
        for (int i = start; i < stop; i++) {
            snippet.add(positions.contains(i) ? "<b>" + words[i] + "</b>" : words[i]);
        }
        return snippet.toString();
    }
}
