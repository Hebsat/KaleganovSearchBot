package main.repository;

import main.model.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PageRepository extends CrudRepository<Page, Integer> {

    @Query(value = "SELECT p FROM Page p WHERE p.path = :path")
    Page findByPath(@Param("path") String path);
}
