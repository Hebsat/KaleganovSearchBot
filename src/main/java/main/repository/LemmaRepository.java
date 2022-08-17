package main.repository;

import main.model.Lemma;
import main.model.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface LemmaRepository extends CrudRepository<Lemma, Integer> {

    @Query(value = "SELECT l FROM Lemma l WHERE l.lemma = :lemma AND site_id = :siteId")
    Lemma findByLemma(@Param("lemma") String lemma, @Param("siteId") int siteId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO lemmas (lemma, site_id, frequency) VALUES (:lemma, :siteId, 1) ON DUPLICATE KEY UPDATE frequency = frequency + 1", nativeQuery = true)
    void addLemma(@Param("lemma") String lemma, @Param("siteId") int siteId);

    @Query(value = "SELECT COUNT(*) FROM lemmas WHERE site_id = :id", nativeQuery = true)
    long findCountBySiteId(@Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Lemma l WHERE site_id = :siteId")
    void deleteBySiteId(@Param("siteId") int siteId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Lemma l SET frequency = frequency - 1 WHERE l = :lemma")
    void decreaseFrequency(@Param("lemma") Lemma lemma);
}
