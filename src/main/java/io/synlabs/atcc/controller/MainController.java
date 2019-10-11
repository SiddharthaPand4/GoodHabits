package io.synlabs.atcc.controller;

import io.synlabs.atcc.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@CrossOrigin(origins = "http://localhost:3000")
@Controller
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);


    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

}
