package com.example.lake_catalog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Optional;
import com.example.lake_catalog.model.*;
import com.example.lake_catalog.service.*;

import jakarta.servlet.http.HttpSession;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private LakeService lakeService;

    @GetMapping("/profile/current")
    public String currentUserProfile(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/"; // Если пользователь не авторизован, перенаправляем на страницу входа
        }
        return "redirect:/users/profile/" + currentUser.getId(); // Перенаправление на страницу профиля текущего пользователя
    }

    @GetMapping("/profile/{userId}")
public String getUserProfile(@PathVariable Long userId, Model model) {
    Optional<User> userOptional = userService.findUserById(userId);
    if (userOptional.isPresent()) {
        User user = userOptional.get();
        model.addAttribute("user", user);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.forLanguageTag("ru"));
        LocalDate creationDate = user.getCreationDate();
        String formattedDate = (creationDate != null) ? creationDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "Дата не указана";

        model.addAttribute("formattedDate", formattedDate);

        //Lake lakePage = lakeService.getLakePageByUserId(userId); // Например, метод для получения lakePage
        //model.addAttribute("lakePage", lakePage); // Передаем в шаблон
        return "profile/profile";
    } else {
        return "error/404"; // Возвращаем страницу ошибки
    }
}

}
