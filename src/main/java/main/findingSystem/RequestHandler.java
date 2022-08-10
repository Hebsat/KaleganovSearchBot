package main.findingSystem;

import main.indexingPages.ParseData;
import main.lemmatization.LemmaCollector;
import main.lemmatization.Lemmatizer;
import main.model.Lemma;
import main.model.Page;
import main.model.Site;
import main.repository.Repositories;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@Component
public class RequestHandler {

    private final Repositories repositories;
    private final List<Lemma> requestLemmas;

    public RequestHandler(Repositories repositories) {
        this.repositories = repositories;
        requestLemmas = new ArrayList<>();
    }

    public List<ResponseObject> requestHandler(String request, Site site) {
        List<ResponseObject> result = new ArrayList<>();
        ParseData.setSearching(true);
        new LemmaCollector().getLemmas(request).keySet().forEach(l -> requestLemmas.add(getLemma(l, site)));
        ParseData.setSearching(false);
//        requestLemmas.removeIf(Objects::isNull);
        if (requestLemmas.contains(null) | requestLemmas.isEmpty()) {
            return new ArrayList<>();
        }
        requestLemmas.sort(Comparator.comparing(Lemma::getFrequency));
        pageListCreator(requestLemmas).forEach(page -> {
            try {
                result.add(getResponceObject(page));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        result.sort(Comparator.comparing(ResponseObject::getRelevance));
        Collections.reverse(result);
        return result;
    }

    private Lemma getLemma(String lemma, Site site) {
        return repositories.getLemmaRepository().findByLemma(lemma, site.getId());
    }

    private List<Page> getPages(Lemma lemma) {
        List<Page> pages = new ArrayList<>();
        lemma.getIndexes().forEach(i -> {
            Page page = new Page(
                    i.getPage().getId(),
                    i.getPage().getPath(),
                    i.getPage().getCode(),
                    i.getPage().getContent(),
                    i.getPage().getSite(),
                    i.getRank());
            pages.add(page);
        });
        Logger.getLogger(RequestHandler.class.getName()).info(lemma.getLemma() + " - " + pages.size());
        return pages;
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

    private ResponseObject getResponceObject(Page page) throws IOException {
        ResponseObject responceObject = new ResponseObject();
        responceObject.setUri(page.getPath());
        responceObject.setTitle(getPageTitle(page));
        responceObject.setRelevance(page.getRelevance());
//        responceObject.setSnippet(getSnippet(page));
        return responceObject;
    }

    private String getPageTitle(Page page) {
        return Jsoup.parse(page.getContent()).getElementsByTag("title").text();
    }

    private String getSnippet(Page page) throws IOException {
        String snippet = "";
        String text = Jsoup.parse(page.getContent()).getAllElements().text();
        String[] words = text.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            if (wordValidator(words[i])) {
//                if (i > 4) {
//                    for (int j = 5; j > 0; j--) {
//                        snippet = snippet.concat(words[i - j]).concat(" ");
//                    }
//                }
//                else if(i > 0) {
//                    for (int j = 0; j < i; j++) {
//                        snippet = snippet.concat(words[j]).concat(" ");
//                    }
//                }
                snippet = snippet.concat("<b>").concat(words[i]).concat("</b>");
            }
        }
        return snippet;
    }

    private boolean wordValidator(String word) throws IOException {
        word = new LemmaCollector().wordFormatterToLowerCase(word);
        if (!new LemmaCollector().wordValidator(word)) {
            return false;
        }
        for (String wordLemma : new Lemmatizer(word).call()) {
            for (Lemma requestLemma : requestLemmas) {
                if (requestLemma.getLemma().equals(wordLemma)) {
                    return true;
                }
            }
        }
        return false;
    }
}
