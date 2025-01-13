package org.example.demospring.controller;

import org.example.demospring.entity.User;
import org.example.demospring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Главная страница
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // Форма логина (GET /login)
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Форма регистрации (GET /register)
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        // Получаем список всех пользователей из репозитория
        List<User> allUsers = userRepository.findAll();

        // Кладём список пользователей в модель, чтобы Thymeleaf мог их отобразить
        model.addAttribute("users", allUsers);

        // Возвращаем имя шаблона (users.html)
        return "users";
    }


    // Обработка данных формы регистрации (POST /do-register)
    @PostMapping("/do-register")
    public String doRegister(@ModelAttribute("user") User user) {
        // Шифруем пароль перед сохранением
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/login";
    }
}
