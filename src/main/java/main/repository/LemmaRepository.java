package main.repository;

import main.model.Lemma;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface LemmaRepository extends CrudRepository<Lemma, Integer> {

    @Query(value = "SELECT l FROM Lemma l WHERE l.lemma = :lemma")
    Lemma findByLemma(@Param("lemma") String lemma);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO lemmas (lemma, frequency) VALUES (:lemma, 1) ON DUPLICATE KEY UPDATE frequency = frequency + 1", nativeQuery = true)
    void addLemma(@Param("lemma") String lemma);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO lemmas (lemma, frequency) VALUES (:multiInsertQuery) ON DUPLICATE KEY UPDATE frequency = frequency + 1", nativeQuery = true)
    void addAllLemmas(@Param("multiInsertQuery") String multiInsertQuery);
}
