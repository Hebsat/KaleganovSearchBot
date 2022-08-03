package main.repository;

import main.model.Site;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SiteRepository extends CrudRepository<Site, Integer> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO sites (status, status_time, url, name) VALUES ('INDEXING', NOW(), :url, :name)",
            nativeQuery = true)
    void startIndexingSite(@Param("url") String url, @Param("name") String name);

    @Modifying
    @Transactional
    @Query(value = "UPDATE sites SET status_time =  NOW() WHERE id = :id", nativeQuery = true)
    void updateIndexingDate(@Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE sites SET status = 'INDEXED', status_time = NOW() WHERE id = :id", nativeQuery = true)
    void finishIndexingSite(@Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE sites SET status = 'FAILED', status_time = NOW(), last_error = :error WHERE id = :id",
            nativeQuery = true)
    void failedIndexingSite(@Param("id") int id, @Param("error") String error);

    @Modifying
    @Transactional
    @Query(value = "UPDATE sites SET status = 'FAILED', status_time = NOW(), last_error = :error WHERE status = 'INDEXING'",
            nativeQuery = true)
    void stopIndexingSites(@Param("error") String error);

    @Query(value = "SELECT s FROM Site s WHERE s.url = :url and s.status = 'INDEXED'")
    Site findIndexedSiteByUrl(@Param("url") String url);

    @Query(value = "SELECT s FROM Site s WHERE s.url = :url and s.status = 'INDEXING'")
    Site findIndexingSiteByUrl(@Param("url") String url);

    @Query(value = "SELECT s FROM Site s WHERE s.url = :url and s.status = 'FAILED'")
    Site findFailedSiteByUrl(@Param("url") String url);
}
