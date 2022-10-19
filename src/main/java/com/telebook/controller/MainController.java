package com.telebook.controller;

import com.telebook.entities.Complaints;
import com.telebook.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class MainController {

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "Home");
        return "index";
    }

    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Login");
        return "login";
    }

    @RequestMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title", "SignUp");
        model.addAttribute("user", new User());
        return "signup";
    }

    @RequestMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About");
        return "about";
    }

    @RequestMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("complaint", new Complaints());
        model.addAttribute("title", "Contact");
        return "contact";
    }
}
