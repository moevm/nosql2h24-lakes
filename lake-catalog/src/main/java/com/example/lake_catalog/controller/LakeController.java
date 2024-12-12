// LakeController.java
package com.example.lake_catalog.controller;

import com.example.lake_catalog.service.LakeService;
import com.example.lake_catalog.service.UserService;

import jakarta.servlet.http.HttpSession;

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
import com.example.lake_catalog.repository.LakeRepository;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/lakes")
public class LakeController {

    private final LakeService lakeService;

    @Autowired
    private UserService userService;

    @Autowired
    private LakeRepository lakeRepository;

    @Autowired
    public LakeController(LakeService lakeService) {
        this.lakeService = lakeService;
    }

    @GetMapping("/main")
    public String showMainPage(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(required = false) String name,
                               @RequestParam(required = false) String depth,
                               @RequestParam(required = false) String square,
                               @RequestParam(required = false) String city,
                               @RequestParam(required = false) String region,
                               @RequestParam(required = false) String rating,
                               HttpSession session) {
        int pageSize = 8; // Количество озер на одной странице

        // Вызываем метод фильтрации
        Page<Lake> lakePage = lakeService.filterLakes(name, depth, square, city, region, rating, page, pageSize);

        // Параметры пагинации
        int totalPages = lakePage.getTotalPages();
        int startPage = Math.max(0, page - 1);
        int endPage = Math.min(totalPages - 1, page + 1);
        User currentUser = (User) session.getAttribute("currentUser");
        if(currentUser!=null){
            model.addAttribute("userPhoto", currentUser.getPhoto());
        } else {
            model.addAttribute("userPhoto", "https://www.hydropower.ru/en/auth/inside.png");
        }
        // Передаем данные в модель
        model.addAttribute("lakePage", lakePage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("name", name);
        model.addAttribute("depth", depth);
        model.addAttribute("square", square);
        model.addAttribute("city", city);
        model.addAttribute("region", region);
        model.addAttribute("rating", rating);
        return "main/main"; // Возвращаем имя представления
    }
    
    @GetMapping("/lake_page/{id}")
    public String showLakeDetails(@PathVariable("id") Long id, Model model, HttpSession session) {
        // Загружаем озеро по id
        Optional<Lake> lakeOptional = lakeService.findLakeById(id);
        
        if (lakeOptional.isPresent()) {
            Lake lake = lakeOptional.get();
            model.addAttribute("lake", lake);
            User currentUser = (User) session.getAttribute("currentUser");

            boolean isInWantVisit = false;
            boolean isInVisited = false;
            if (currentUser != null) {
                isInWantVisit = userService.isLakeInWantVisit(currentUser.getId(), lake.getId());
                isInVisited = userService.isLakeInVisited(currentUser.getId(), lake.getId());
            }
            model.addAttribute("isInWantVisit", isInWantVisit);
            model.addAttribute("isInVisited", isInVisited);

            return "card/card"; // Имя представления для отображения информации об озере
        } else {
            return "error"; // Если озеро не найдено
        }
    }

    @PostMapping("/{lakeId}/action")
    public ResponseEntity<?> handleLakeAction(@PathVariable Long lakeId, @RequestBody Map<String, String> request, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Вы не авторизованы."));
        }

        String action = request.get("action");
        Optional<Lake> optionalLake = lakeService.findLakeById(lakeId);

        if (optionalLake.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Озеро не найдено."));
        }

        Lake lake = optionalLake.get();

        switch (action) {
            case "want_visit":
                userService.addWantVisitLake(currentUser, lake);
                return ResponseEntity.ok(Map.of("message", "Добавлено в 'Хочу посетить'."));
            case "remove_want_visit":
                userService.removeWantVisitLake(currentUser.getId(), lake.getId());
                return ResponseEntity.ok(Map.of("message", "Удалено из 'Хочу посетить'."));
            case "visited":
                userService.addVisitedLake(currentUser, lake);
                return ResponseEntity.ok(Map.of("message", "Добавлено в 'Уже посетил'."));
            case "remove_visited":
                userService.removeVisitedLake(currentUser.getId(), lake.getId());
                return ResponseEntity.ok(Map.of("message", "Удалено из 'Уже посетил'."));
            default:
                return ResponseEntity.badRequest().body(Map.of("message", "Неизвестное действие."));
        }
    }

    @GetMapping("/{lakeId}/status")
    public ResponseEntity<Map<String, Boolean>> getLakeStatus(@PathVariable Long lakeId, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            // Пользователь не авторизован, возвращаем ответ с сообщением в виде Map<String, Boolean>
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("isWantVisit", false, "isVisited", false));
        }

        Optional<Lake> optionalLake = lakeService.findLakeById(lakeId);
        if (optionalLake.isEmpty()) {
            // Озеро не найдено, возвращаем ответ с сообщением в виде Map<String, Boolean>
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("isWantVisit", false, "isVisited", false));
        }

        Lake lake = optionalLake.get();
        boolean isInWantVisit = userService.isLakeInWantVisit(currentUser.getId(), lake.getId());
        boolean isInVisited = userService.isLakeInVisited(currentUser.getId(), lake.getId());

        // Возвращаем информацию о статусе озера в виде Map<String, Boolean>
        return ResponseEntity.ok(Map.of(
            "isWantVisit", isInWantVisit,
            "isVisited", isInVisited
        ));
    }



    @GetMapping()
    public List<Lake> getAllLakes() {
        return lakeService.findAll();
    }

    @GetMapping("/initialize")
    public ResponseEntity<Void> initialize() {
        lakeService.initialize();
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/lakes")).build();
    }

    @GetMapping("/regions")
    public List<String> getAllRegions() {
        return lakeRepository.findAllRegions();
    }

    // Получение всех городов для выпадающего списка
    @GetMapping("/cities")
    public List<String> getAllCities() {
        return lakeRepository.findAllCities();
    }
}
