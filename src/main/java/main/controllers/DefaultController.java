package main.controllers;

import main.services.IndexingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {

    @Autowired
    private IndexingService indexingService;

    @RequestMapping(value = "/admin")
    public String index() {
        return "index";
    }
}
