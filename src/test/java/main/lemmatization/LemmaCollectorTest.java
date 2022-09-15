package main.lemmatization;

import main.services.lemmatization.LemmaCollector;
import main.model.LemmaValues;
import main.model.Word;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LemmaCollectorTest {

    private final LemmaCollector lemmaCollector = new LemmaCollector();
    private String query;

    @BeforeEach
    void setUp() {
        query = "\"Перрон,           перрон!" + System.lineSeparator() + "На   перроне?\"";
//        result = new HashMap<>();
    }

    @AfterEach
    void tearDown() {
        query = null;
//        result = null;
    }

    @Test
    void getLemmas() {
        Map<String, LemmaValues> result = lemmaCollector.getLemmas(query);
        assertEquals(1, result.size());
        assertEquals(3, result.get("перрон").getCount());
    }

    @Test
    void wordFormatterToLowerCase() {
        assertTrue(lemmaCollector.wordFormatterToLowerCase(new Word("аБ.,!?'\"")).getWord().matches("аб"));
    }

    @Test
    void wordValidator() {
        assertTrue(lemmaCollector.wordValidator(new Word("абвгдеёэюя")));
        assertFalse(lemmaCollector.wordValidator(new Word("аё я")));
        assertFalse(lemmaCollector.wordValidator(new Word("123")));
        assertFalse(lemmaCollector.wordValidator(new Word("АЁЯ")));
        assertFalse(lemmaCollector.wordValidator(new Word("abc")));
        assertFalse(lemmaCollector.wordValidator(new Word("а,")));
    }
}