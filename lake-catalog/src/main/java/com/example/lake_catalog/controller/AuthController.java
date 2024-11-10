package com.example.lake_catalog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AuthController {

    // Первая страница
    @GetMapping("/")
    public String homePage() {
        return "home";
    }

    // Страница входа
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Страница регистрации
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // Обработка формы входа
    @PostMapping("/perform_login")
    public String performLogin(String email, String password) {
        // Добавить логику аутентификации
        boolean loginSuccess = true; // Заглушка, добавить проверку email и password

        if (loginSuccess) {
            return "redirect:/lakes"; // Переход на главную страницу после успешного входа
        } else {
            return "redirect:/login?error"; // Переход обратно на страницу входа с ошибкой
        }
    }

    // Обработка формы регистрации
    @PostMapping("/perform_register")
    public String performRegister(String nickname, String email, String password, String confirmPassword) {
        // Логика для проверки данных и создания нового пользователя
        if (!password.equals(confirmPassword)) {
            return "redirect:/register?error=password_mismatch";
        }
        
        // Надо добавить код для сохранения пользователя в базе данных
        return "redirect:/"; // Переход на первую страницу после успешной регистрации
    }
}
