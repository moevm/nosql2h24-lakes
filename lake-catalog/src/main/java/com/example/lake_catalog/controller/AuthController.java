// AuthController.java
package com.example.lake_catalog.controller;

import com.example.lake_catalog.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Первая страница
    @GetMapping("/")
    public String homePage() {
        return "home/home";
    }

    // Страница входа
    @GetMapping("/login")
    public String loginPage() {
        return "login/login";
    }

    @GetMapping("/filtration")
    public String filterPage() {
        return "filtration/filtration";
    }

    // Страница регистрации
    @GetMapping("/register")
    public String registerPage() {
        return "register/register";
    }

    // Перенаправление на страницу /lakes/main после входа
    @GetMapping("/main")
    public String mainPage() {
        return "redirect:/lakes/main";
    }

    // Обработка формы входа
    @PostMapping("/perform_login")
    public String performLogin(String email, String password) {
        try {
            authService.loginUser(email, password);
            return "redirect:/lakes/main"; // Редирект на страницу озёр после успешного входа
        } catch (RuntimeException e) {
            return "redirect:/login?error"; // Переход обратно на страницу входа с ошибкой
        }
    }

    // Обработка формы регистрации
    @PostMapping("/perform_register")
    public String performRegister(String username, String email, String password, String confirm) {
        if (!password.equals(confirm)) {
            return "redirect:/register?error=password_mismatch";
        }
        try {
            authService.registerUser(username, email, password);
        } catch (RuntimeException e) {
            return "redirect:/register?error=password_mismatch";
        }
        return "redirect:/lakes/main";
    }
}
