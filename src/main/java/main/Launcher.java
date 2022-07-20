package main;

import main.model.PageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ForkJoinPool;

@Component
public class Launcher implements CommandLineRunner {

    @Autowired
    private PageRepository pageRepository;

    @Override
    public void run(String... args) throws Exception {

        String url1 = "http://www.playback.ru/";
        String url2 = "https://volochek.life/";
        String url3 = "http://radiomv.ru/";
        String url4 = "https://ipfran.ru/";
        String url5 = "https://dimonvideo.ru/";

        new ForkJoinPool().invoke(new LinksParser(url1, pageRepository));
        System.out.println("---------------------------------------------------------------------------------------");
        System.out.println("Всего ссылок найдено: " + FoundLinks.getFoundLinks().size());
        System.out.println("Ссылок в БД         : " + pageRepository.count());
    }
}
