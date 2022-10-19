package com.telebook.controller;

import com.telebook.entities.Contact;
import com.telebook.entities.User;
import com.telebook.repositories.ContactRepository;
import com.telebook.repositories.UserRepository;
import com.telebook.services.Message;
import com.telebook.services.PasswordHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/people")
public class UserServiceController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ContactRepository contactRepo;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @ModelAttribute
    private void addUser(Model model, Principal principal) {
        model.addAttribute("user",
                this.userRepo.findByEmail(principal.getName()).get()
        );
    }

    @PostMapping("/change-password")
    private String userSettings(@Valid @ModelAttribute("passwordHelper") PasswordHelper passwordHelper, BindingResult result,
                                Principal principal, Model model, HttpSession session) {

        model.addAttribute("title", "Change Password");
        User user = this.userRepo.findByEmail(principal.getName()).get();

        try {
            if (!encoder.matches(passwordHelper.getOldPassword(), user.getPassword())) {
                result.addError(new FieldError("passwordHelper", "oldPassword",
                        "Please Enter Correct Old Password"));
            }
            if (encoder.matches(passwordHelper.getNewPassword(), user.getPassword())) {
                result.addError(new FieldError("passwordHelper", "newPassword",
                        "Please Enter a different Password"));
            }
            if (!passwordHelper.getNewPassword().equals(passwordHelper.getRePassword())) {
                result.addError(new FieldError("passwordHelper", "rePassword",
                        "Password mismatch!!"));
            }
            if (result.hasErrors()) {
                return "/user/pChange";
            }

            user.setPassword(encoder.encode(passwordHelper.getNewPassword()));
            this.userRepo.save(user);
            session.setAttribute("message", new Message("Password Changes successfully!!", "success"));

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", new Message("something went wrong!! " + e.getMessage(), "error"));
        }
        return "redirect:/user/settings";
    }

    @PostMapping("/update-user")
    private String updateProfile(Principal principal,
                                 @ModelAttribute("user") User user,
                                 @RequestParam("image") MultipartFile image,
                                 HttpSession session) throws IOException {

        String username = principal.getName();
        User oldUser = this.userRepo.findByEmail(username).get();

        if (image.isEmpty()) {
            user.setProfile(oldUser.getProfile());
        } else {
            // deleting image
            File avatar = new ClassPathResource("static/img/user_profile").getFile();
            File imgFile = new File(avatar, oldUser.getId() + "_" + oldUser.getProfile());
            imgFile.delete();

            user.setProfile(image.getOriginalFilename());
            File pathFile = new ClassPathResource("static/img/user_profile").getFile();
            Files.copy(
                    image.getInputStream(), //input stream
                    Paths.get(pathFile.getAbsolutePath() + File.separator + user.getId() + "_" + image.getOriginalFilename()),
                    StandardCopyOption.REPLACE_EXISTING
            );
        }

        this.userRepo.save(user);
        session.setAttribute("message", new Message("Success!! Updated Details...", "success"));
        return "redirect:/user/";
    }

    @PostMapping("/create")
    public String addContact(
            @Valid @ModelAttribute("contact") Contact contact, BindingResult result,
            @RequestParam("profile") MultipartFile profile,
            Model model, Principal principal, HttpSession session) {

        model.addAttribute("title", "New Contact");

        if (result.hasErrors()) {
            model.addAttribute("contact", contact);
            return "user/new";
        }

        // getting user details
        User user = this.userRepo.findByEmail(principal.getName()).get();
        try {
            // uploading image file
            if (profile.isEmpty()) {
                contact.setAvatar("avatar.png");

            } else {
                contact.setAvatar(profile.getOriginalFilename());
                File pathFile = new ClassPathResource("static/img/contact_img").getFile();
                Files.copy(
                        profile.getInputStream(), //input stream
                        Paths.get(pathFile.getAbsolutePath() + File.separator + user.getId() + "_" + profile.getOriginalFilename()),
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            contact.setUser(user);
            user.getContacts().add(contact);

            this.userRepo.save(user);

            session.setAttribute("message", new Message("Successfully Added Contact. Add another?", "success"));
            model.addAttribute("contact", new Contact());

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", new Message("something went wrong!! Please check details!!", "error"));
            model.addAttribute("contact", contact);
        }
        return "user/new";
    }

    @RequestMapping("/delete/{id}")
    public String deleteContact(@PathVariable("id") int id, Model model, HttpSession session) throws IOException {
        Contact contact = this.contactRepo.findById(id).get();

        // deleting image
        File avatar = new ClassPathResource("static/img/contact_img").getFile();
        File imgFile = new File(avatar, contact.getUser().getId() + "_" + contact.getAvatar());
        imgFile.delete();

        contact.setUser(null);
        this.contactRepo.delete(contact);

        session.setAttribute("message", new Message("Contact Deleted Successfully!!", "success"));
        return "redirect:/user/show";
    }

    @PostMapping("/updates")
    public String updateContact(@Valid @ModelAttribute("contact") Contact contact, BindingResult result,
                                @RequestParam("id") int id, Model model,
                                @RequestParam("profile") MultipartFile profile,
                                Principal principal, HttpSession session) {

        Contact contactOld = this.contactRepo.findById(id).get();
        User user = this.userRepo.findByEmail(principal.getName()).get();

        if (result.hasErrors()) {
            contact.setAvatar(contactOld.getAvatar());
            model.addAttribute("title", "Update Contact");
            model.addAttribute("contact", contact);
            return "user/update";
        }

        try {
            if (profile.isEmpty()) {
                contact.setAvatar(contactOld.getAvatar());
            } else {
                // deleting old image
                File oldAvatar = new ClassPathResource("static/img/contact_img").getFile();
                File oldfile = new File(oldAvatar, user.getId() + "_" + contactOld.getAvatar());
                oldfile.delete();

                // saving new image
                contact.setAvatar(profile.getOriginalFilename());
                File avatar = new ClassPathResource("static/img/contact_img").getFile();
                Files.copy(
                        profile.getInputStream(), //input stream
                        Paths.get(avatar.getAbsolutePath() + File.separator + user.getId() + "_" + profile.getOriginalFilename()),
                        StandardCopyOption.REPLACE_EXISTING
                );
            }
            contact.setUser(user);
            this.contactRepo.save(contact);

            session.setAttribute("message", new Message("Successfully Updated Contact!!", "success"));
            model.addAttribute("contact", new Contact());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/user/show";
    }

    @RequestMapping("/search")
    private String searchName(@RequestParam("search") String name,
                              Model model, Principal principal) {

        model.addAttribute("title", "Show Contact");

        User user = this.userRepo.findByEmail(principal.getName()).get();
        model.addAttribute("user", user);

        List<Contact> contactList = this.contactRepo.getContactByUserId(user.getId());
        List<Contact> searchList = contactList.stream()
                .filter(c -> c.getName().toLowerCase().equals(name.toLowerCase()))
                .collect(Collectors.toList());
        model.addAttribute("contacts", searchList);

        return "user/show";
    }
}
