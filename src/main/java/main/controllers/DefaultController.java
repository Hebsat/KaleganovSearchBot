package main.controllers;

import main.services.IndexingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {

    @Autowired
    private IndexingService indexingService;

    @RequestMapping(value = "/admin")
    public String index(Model model) {
        model.addAttribute("sitesCount", indexingService.getSitesCount());
        model.addAttribute("pagesCount", indexingService.getPagesCount());
        model.addAttribute("lemmasCount", indexingService.getLemmasCount());
        model.addAttribute("allSites", indexingService.getAllSites());
        model.addAttribute("sites", indexingService.getAllIndexedSites());
        model.addAttribute("isIndexing", indexingService.isIndexing());
        return "index";
    }
}
