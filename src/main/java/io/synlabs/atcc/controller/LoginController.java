package io.synlabs.atcc.controller;

import io.synlabs.atcc.entity.AtccUser;
import io.synlabs.atcc.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "login";
    }

    @PostMapping("/login_check")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password) {
        logger.info("Inside login {} {}", username, password);
        AtccUser user = userService.checkLogin(username, password);

        if (user == null) {
            return "redirect:/login?error";
        }
        else{
            return "redirect:/";
        }
    }
}
