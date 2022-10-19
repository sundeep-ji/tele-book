package com.telebook.controller;

import com.telebook.entities.Complaints;
import com.telebook.entities.User;
import com.telebook.repositories.ComplaintRepository;
import com.telebook.repositories.UserRepository;
import com.telebook.security.TeleUserDetails;
import com.telebook.services.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@Controller
public class ServicesController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ComplaintRepository complaintRepo;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, BindingResult result,
                           Model model, HttpSession session) {
        model.addAttribute("title", "SignUp");

        try {
            if (this.userRepo.existsUserByEmail(user.getEmail())) {
                result.addError(new FieldError("user", "email",
                        "Email Address already in use!!"));
            }
            if (user.getRePassword().isEmpty()) {
                result.addError(new FieldError("user", "rePassword",
                        "This field can't be empty!!"));
            }
            if (!user.getPassword().isEmpty() && !user.getRePassword().isEmpty()) {
                if (!user.getPassword().equals(user.getRePassword())) {
                    result.addError(new FieldError("user", "rePassword",
                            "Password Mismatch!!"));
                }
            }
            if (result.hasErrors()) {
                model.addAttribute("user", user);
                return "signup";
            }
            // encoding password
            user.setPassword(encoder.encode(user.getPassword()));

            this.userRepo.save(user);
            session.setAttribute("message", new Message(
                    "Successfully Registered!! Welcome to Tele-Book!!",
                    "success"));

            loginOnSignup(user);
            return "user/dashboard";


        } catch (Exception e) {
            model.addAttribute("user", user);
            session.setAttribute(
                    "message", new Message(
                            "Please Try Again! " + e.getMessage(),
                            "error"
                    )
            );
        }
        return "signup";
    }

    // method for direct login after signup
    private void loginOnSignup(User user) {
        UserDetails details = new TeleUserDetails(user);
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(details, null,
                                details.getAuthorities()
                        )
                );
    }

    @PostMapping("/request")
    private String requestHelp(@ModelAttribute("complaint") Complaints complaints,
                               Model model, HttpSession session) {
        this.complaintRepo.save(complaints);

        model.addAttribute("title", "Contact");
        model.addAttribute("complaint", new Complaints());

        session.setAttribute("message", new Message("Request Sent successfully!", "success"));
        return "contact";
    }


}
