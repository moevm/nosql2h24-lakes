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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Optional;
import com.example.lake_catalog.model.*;
import com.example.lake_catalog.repository.LakeRepository;
import com.example.lake_catalog.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private LakeService lakeService;

    @Autowired
    private LakeRepository lakeRepository;

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
            List<Lake> wantVisitLakes = userService.getWantVisitLakes(userId);
            List<Lake> visitedLakes = userService.getVisitedLakes(userId);
            model.addAttribute("user", user);
            model.addAttribute("wantVisitLakes", wantVisitLakes);
            model.addAttribute("visitedLakes", visitedLakes);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.forLanguageTag("ru"));
            LocalDate creationDate = user.getCreationDate();
            String formattedDate = (creationDate != null) ? creationDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "Дата не указана";

            model.addAttribute("formattedDate", formattedDate);

            LocalDateTime editDate = user.getEditDate();
            String formatted_editDate = (editDate != null) ? editDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "Дата не указана";

            model.addAttribute("formatted_editDate", formatted_editDate);

            //Lake lakePage = lakeService.getLakePageByUserId(userId); // Например, метод для получения lakePage
            //model.addAttribute("lakePage", lakePage); // Передаем в шаблон
            return "profile/profile";
        } else {
            return "error/404"; // Возвращаем страницу ошибки
        }
    }

    @PostMapping("/check-email-unique")
    public ResponseEntity<Map<String, Boolean>> checkEmailUnique(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        boolean isUnique = userService.isEmailUnique(email);
        return ResponseEntity.ok(Collections.singletonMap("isUnique", isUnique));
    }

    @PutMapping("/profile/{userId}/update-profile")
    public ResponseEntity<Map<String, String>> updateUserProfile(
            @PathVariable Long userId, // Заменили @RequestParam на @PathVariable для userId
            @RequestParam String newName,
            @RequestParam String newEmail) {
        try {
            userService.updateUserProfile(userId, newName, newEmail);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Профиль обновлен успешно!");
            response.put("editDate", LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage()); // Возвращаем ошибку как часть ответа
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/profile/{userId}/imp_exp/export")
    public void exportLakes(HttpServletResponse response) throws IOException {
        List<Lake> lakes = lakeRepository.findAll();

        // Преобразуем каждый объект в нужный формат
        List<Map<String, Object>> exportData = new ArrayList<>();
        for (Lake lake : lakes) {
            Map<String, Object> lakeData = new HashMap<>();
            Map<String, Object> properties = new HashMap<>();
            properties.put("square", lake.getSquare());
            properties.put("depth", lake.getDepth());
            properties.put("city", lake.getCity());
            properties.put("name", lake.getName());
            properties.put("rating", lake.getRating());
            properties.put("description", lake.getDescription());
            properties.put("region", lake.getRegion());
            properties.put("photos", lake.getPhotos());

            Map<String, Object> lakeItem = new HashMap<>();
            lakeItem.put("identity", lake.getId());  // предполагаем, что getId() возвращает идентификатор
            lakeItem.put("labels", List.of("Lake"));
            lakeItem.put("properties", properties);

            Map<String, Object> wrappedLake = new HashMap<>();
            wrappedLake.put("l", lakeItem);

            exportData.add(wrappedLake);
        }

        // Конвертируем данные в JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(exportData);

        // Устанавливаем заголовки ответа
        response.setContentType("application/json; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=lakes.json");
        response.getWriter().write(json);
    }

    @GetMapping("/imp_exp")
    public String loginPage() {
        return "import-export/import-export";
    }

    @PostMapping("/profile/{userId}/imp_exp/import")
    public ResponseEntity<String> importLakes(@RequestBody List<Map<String, Object>> lakesData) {
        try {
            for (Map<String, Object> lakeData : lakesData) {
                Map<String, Object> lData = (Map<String, Object>) lakeData.get("l");
    
                // Из "l" извлекаем "properties"
                Map<String, Object> properties = (Map<String, Object>) lData.get("properties");
                
                String name = (String) properties.get("name");
                
                // Проверяем, существует ли озеро с таким названием
                Optional<Lake> existingLake = lakeRepository.findByName(name);
                
                if (existingLake.isPresent()) {
                    // Обновляем свойства существующего озера
                    Lake lake = existingLake.get();
                    lake.setRegion((String) properties.get("region"));
                    lake.setCity((String) properties.get("city"));
                    lake.setRating((Double) properties.get("rating"));
                    lake.setDepth((Double) properties.get("depth"));
                    lake.setSquare((Double) properties.get("square"));
                    lake.setDescription((String) properties.get("description"));
                    lake.setPhotos((List<String>) properties.get("photos"));
                    
                    lakeRepository.save(lake);
                } else {
                    // Создаем новое озеро
                    Lake lake = new Lake();
                    lake.setName(name);
                    lake.setRegion((String) properties.get("region"));
                    lake.setCity((String) properties.get("city"));
                    lake.setRating((Double) properties.get("rating"));
                    lake.setDepth((Double) properties.get("depth"));
                    lake.setSquare((Double) properties.get("square"));
                    lake.setDescription((String) properties.get("description"));
                    lake.setPhotos((List<String>) properties.get("photos"));
                    
                    lakeRepository.save(lake);
                }
            }
            
            return ResponseEntity.ok("Озера успешно импортированы.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка импорта данных.");
        }
    }

}