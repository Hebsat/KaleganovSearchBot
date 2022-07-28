package main;

import main.indexingPages.FoundLinks;
import main.indexingPages.LinksParser;
import main.model.Field;
import main.properties.SearchBotProperties;
import main.repository.Repositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ForkJoinPool;

@Component
public class Launcher implements CommandLineRunner {

    @Autowired
    private Repositories repositories;

    @Autowired
    private SearchBotProperties searchBotProperties;

    @Override
    public void run(String... args) throws Exception {
        long start = System.currentTimeMillis();
        List<Field> fields = new ArrayList<>((Collection<Field>) repositories.getFieldRepository().findAll());

        new ForkJoinPool(Runtime.getRuntime().availableProcessors() / searchBotProperties.getThreadNumber())
                .invoke(new LinksParser(searchBotProperties.getLink(), repositories, fields));
        System.out.println("---------------------------------------------------------------------------------------");
        System.out.println("Всего ссылок найдено: " + FoundLinks.getFoundLinks().size());
        System.out.println("Ссылок в БД         : " + repositories.getPageRepository().count());
        System.out.println("Затрачено времени: " + (System.currentTimeMillis() - start) + " ms");
    }
}
