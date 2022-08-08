package main.repository;

import main.model.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PageRepository extends CrudRepository<Page, Integer> {

    @Query(value = "SELECT p FROM Page p WHERE p.path = :path and site_id = :id")
    Page findByPath(@Param("path") String path, @Param("id") int id);

    @Query(value = "SELECT COUNT(*) FROM pages WHERE site_id = :id", nativeQuery = true)
    long findCountBySiteId(@Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Page p WHERE site_id = :siteId")
    void deleteBySiteId(@Param("siteId") int siteId);
}
