package main.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class Repositories {

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private FieldRepository fieldRepository;

    @Autowired
    private IndexRepository indexRepository;

    @Autowired
    private LemmaRepository lemmaRepository;

    @Autowired
    private SiteRepository siteRepository;

    public PageRepository getPageRepository() {
        return pageRepository;
    }

    public void setPageRepository(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    public FieldRepository getFieldRepository() {
        return fieldRepository;
    }

    public void setFieldRepository(FieldRepository fieldRepository) {
        this.fieldRepository = fieldRepository;
    }

    public IndexRepository getIndexRepository() {
        return indexRepository;
    }

    public void setIndexRepository(IndexRepository indexRepository) {
        this.indexRepository = indexRepository;
    }

    public LemmaRepository getLemmaRepository() {
        return lemmaRepository;
    }

    public void setLemmaRepository(LemmaRepository lemmaRepository) {
        this.lemmaRepository = lemmaRepository;
    }

    public SiteRepository getSiteRepository() {
        return siteRepository;
    }

    public void setSiteRepository(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }
}
