package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Arrays;

@Controller
public class MessageController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    MessageRepository messageRepository;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String addUser(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "addUser";
    }

    @PostMapping("/processUser")
    public String processUser(@Valid User user, BindingResult result) {
        if (result.hasErrors()) {
            return "processUser";
        }

        Role userRole = roleRepository.findByRole("USER");
        user.setRoles(Arrays.asList(userRole));

        user.setEnabled(true);

        userRepository.save(user);
        return "redirect:/login";
    }

    @RequestMapping("/messages")
    public String listMessages(Model model, Principal principal) {
        model.addAttribute("user", userRepository.findByUsername(principal.getName()));
        model.addAttribute("messages", messageRepository.findAll());
        return "messageList";
    }

    @GetMapping("/addMessage")
    public String addMessage(Model model, Principal principal) {
        model.addAttribute("user", userRepository.findByUsername(principal.getName()));
        model.addAttribute("message", new Message());
        return "addMessage";
    }

    @PostMapping("/processMessage")
    public String processMessage(@Valid Message message, BindingResult result) {
        if (result.hasErrors()) {
            return "addMessage";
        }

        messageRepository.save(message);
        return "redirect:/messages";
    }
    @RequestMapping("/detail/{id}")
    public String showMessage(@PathVariable("id") long id, Model model){
        model.addAttribute("message", messageRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateMessage(@PathVariable("id") long id, Model model, Principal principal){
        model.addAttribute("message", userRepository.findByUsername(principal.getName()));
        model.addAttribute("message", messageRepository.findById(id).get());
        return "addMessage";
    }

    @RequestMapping("/delete/{id}")
    public String delMessage(@PathVariable("id") long id){
        messageRepository.deleteById(id);
        return "redirect:/messageList";
    }

}
