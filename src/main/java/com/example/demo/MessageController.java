package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class MessageController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    MessageRepository messageRepository;

    @RequestMapping("/")
    public String index(Model model, Principal principal) {
        User currentUser = principal != null ? userRepository.findByUsername(principal.getName()) : null;
        model.addAttribute("user", currentUser);

        model.addAttribute("messages", messageRepository.findAll());
        return "index";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String addUser(Model model) {
        User user = new User();

        Role userRole = roleRepository.findByRole("USER");
        user.setRoles(Arrays.asList(userRole));
        user.setEnabled(true);

        model.addAttribute("user", user);
        return "addUser";
    }

    @RequestMapping("/updateProfile/{id}")
    public String updateProfile(@PathVariable("id") long id, Model model, Principal principal) {
        model.addAttribute("user", userRepository.findByUsername(principal.getName()));
        return "addUser";
    }

    @RequestMapping("/detailProfile/{id}")
    public String showProfile(@PathVariable("id") long id, Model model, Principal principal) {
        model.addAttribute("user", userRepository.findByUsername(principal.getName()));
        return "showProfile";
    }

    @PostMapping("/processUser")
    public String processUser(@Valid User user, BindingResult result) {
        if (result.hasErrors()) {
            return "addUser";
        }

        userRepository.save(user);
        return "redirect:/";
    }

    @RequestMapping("/messages")
    public String listMessages(Model model, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName());
        model.addAttribute("user", currentUser);

        model.addAttribute("messages", messageRepository.findByOwner(currentUser));
        return "messageList";
    }

    @RequestMapping("/tagged/{hashtag}")
    public String taggedMessages(@PathVariable("hashtag") String hashtag, Model model, Principal principal) {
        User currentUser = principal != null ? userRepository.findByUsername(principal.getName()) : null;
        model.addAttribute("user", currentUser);

        model.addAttribute("hashtag", hashtag);

        ArrayList<Message> messages = new ArrayList<>();
        for (Message message : messageRepository.findAll()) {
            List<String> hashtags = Arrays.asList(message.getHashtags().split(","));
            if (hashtags.contains(hashtag)) {
                messages.add(message);
            }
        }
        model.addAttribute("messages", messages);
        return "index";
    }

    @GetMapping("/addMessage")
    public String addMessage(Model model, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName());
        model.addAttribute("user", currentUser);

        Message message = new Message();
        message.setOwner(currentUser);
        message.setDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE));

        model.addAttribute("message", message);
        return "addMessage";
    }

    @PostMapping("/processMessage")
    public String processMessage(@Valid Message message, BindingResult result, Model model, Principal principal) {
        if (result.hasErrors()) {
            // -- This is to prevent "Welcome null" message in the header
            User currentUser = userRepository.findByUsername(principal.getName());
            model.addAttribute("user", currentUser);

            return "addMessage";
        }

        messageRepository.save(message);
        return "redirect:/messages";
    }

    @RequestMapping("/detail/{id}")
    public String showMessage(@PathVariable("id") long id, Model model) {
        model.addAttribute("message", messageRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateMessage(@PathVariable("id") long id, Model model, Principal principal) {
        model.addAttribute("user", userRepository.findByUsername(principal.getName()));
        model.addAttribute("message", messageRepository.findById(id).get());
        return "addMessage";
    }

    @RequestMapping("/delete/{id}")
    public String delMessage(@PathVariable("id") long id) {
        messageRepository.deleteById(id);
        return "redirect:/messages";
    }

}
