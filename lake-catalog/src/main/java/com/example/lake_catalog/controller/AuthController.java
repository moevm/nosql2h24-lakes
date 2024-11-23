// AuthController.java
package com.example.lake_catalog.controller;

import com.example.lake_catalog.service.AuthService;

import jakarta.servlet.http.HttpSession;
import com.example.lake_catalog.model.*;
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
    public String performLogin(String email, String password, HttpSession session) {
        try {
            User user = authService.loginUser(email, password);
            session.setAttribute("currentUser", user);
            System.out.println("Пользователь вошел: " + user.getEmail());
            return "redirect:/lakes/main"; // Редирект на страницу озёр после успешного входа
        } catch (RuntimeException e) {
            return "redirect:/login?error"; // Переход обратно на страницу входа с ошибкой
        }
    }

    // Обработка формы регистрации
    @PostMapping("/perform_register")
    public String performRegister(String username, String email, String password, String confirm, HttpSession session) {
        if (!password.equals(confirm)) {
            return "redirect:/register?error=password_mismatch";
        }
        try {
            authService.registerUser(username, email, password);
            User user = authService.loginUser(email, password); // Автоматический вход после регистрации
            session.setAttribute("currentUser", user);
            System.out.println("Новый пользователь зарегистрирован: " + user.getEmail());
            return "redirect:/lakes/main";
        } catch (RuntimeException e) {
            return "redirect:/register?error=password_mismatch";
        }
    }

        // Выход из профиля
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Уничтожение текущей сессии
        return "redirect:/"; // Перенаправление на главную страницу после выхода
    }

}
