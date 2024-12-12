// AuthController.java
package com.example.lake_catalog.controller;

import com.example.lake_catalog.service.AuthService;

import jakarta.servlet.http.HttpSession;
import com.example.lake_catalog.model.*;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;

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
    public String performLogin(String email, String password, HttpSession session, Model model) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            model.addAttribute("error", "Введите email и пароль.");
            return "login/login"; // Вернуть страницу входа с сообщением об ошибке
        }

        try {
            User user = authService.loginUser(email, password);
            session.setAttribute("currentUser", user);
            System.out.println("Пользователь вошел: " + user.getEmail());
            return "redirect:/lakes/main"; // Редирект при успешном входе
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage()); // Передача текста ошибки в шаблон
            return "login/login"; // Вернуть страницу входа
        }
    }


    // Обработка формы регистрации
    @PostMapping("/perform_register")
    public String performRegister(String username, String email, String password, String confirm, HttpSession session, Model model) {
        if (username == null || username.isEmpty() || email == null || email.isEmpty() || 
            password == null || password.isEmpty() || confirm == null || confirm.isEmpty()) {
            model.addAttribute("error", "Заполните все поля.");
            return "register/register"; // Вернуть страницу регистрации с сообщением об ошибке
        }

        if (!password.equals(confirm)) {
            model.addAttribute("error", "Пароли не совпадают.");
            return "register/register";
        }

        try {
            authService.registerUser(username, email, password);
            User user = authService.loginUser(email, password); // Автоматический вход
            session.setAttribute("currentUser", user);
            System.out.println("Новый пользователь зарегистрирован: " + user.getEmail());
            return "redirect:/lakes/main";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage()); // Передача текста ошибки в шаблон
            return "register/register";
        }
    }


        // Выход из профиля
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Уничтожение текущей сессии
        return "redirect:/"; // Перенаправление на главную страницу после выхода
    }

    @GetMapping("/check-auth")
    @ResponseBody
    public Map<String, Boolean> checkAuth(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        return Map.of("authenticated", currentUser != null);
    }

}
