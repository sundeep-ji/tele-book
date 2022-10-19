package com.telebook.controller;

import com.telebook.entities.Contact;
import com.telebook.entities.User;
import com.telebook.repositories.ContactRepository;
import com.telebook.repositories.UserRepository;
import com.telebook.services.PasswordHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ContactRepository contactRepo;

    @ModelAttribute
    private void userLogin(Model model, Principal principal) {
        model.addAttribute("user",
                this.userRepo.findByEmail(principal.getName()).get()
        );
    }

    @RequestMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("title", "Dashboard");
        return "user/dashboard";
    }

    @RequestMapping("/new")
    public String addContact(Model model) {
        model.addAttribute("title", "New Contact");
        model.addAttribute("contact", new Contact());
        return "user/new";
    }

    @RequestMapping("/show")
    public String showContact(Model model, Principal principal) {
        model.addAttribute("title", "Show Contact");

        User user = this.userRepo.findByEmail(principal.getName()).get();

        List<Contact> contactList = this.contactRepo.getContactByUserId(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("contacts", contactList);

        return "user/show";
    }

    @RequestMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("title", "Settings");
        return "user/settings";
    }

    @GetMapping("/change-password")
    public String changePasswordModel(Model model) {
        model.addAttribute("title", "Change Password");
        model.addAttribute("passwordHelper", new PasswordHelper());
        return "user/pChange";
    }

    @RequestMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "Settings");
        return "user/about";
    }

    @RequestMapping("/update/{id}")
    public String updateContact(@PathVariable("id") int id, Model model) {
        Contact contact = this.contactRepo.findById(id).get();

        model.addAttribute("contact", contact);
        model.addAttribute("title", "Update Contact");
        return "user/update";
    }
}
