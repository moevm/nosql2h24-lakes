// LakeController.java
package com.example.lake_catalog.controller;

import com.example.lake_catalog.model.Lake;
import com.example.lake_catalog.model.Review;
import com.example.lake_catalog.service.LakeService;
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

import java.net.URI;
import java.util.List;

@Controller
@RequestMapping("/lakes")
public class LakeController {

    private final LakeService lakeService;

    @Autowired
    public LakeController(LakeService lakeService) {
        this.lakeService = lakeService;
    }

    @GetMapping("/main")
    public String showMainPage(Model model, 
                               @RequestParam(defaultValue = "0") int page,  // Параметр для текущей страницы пагинации
                               @RequestParam(required = false) String name) { // Параметр для поискового запроса

        int pageSize = 8; // Количество озёр на странице
        Page<Lake> lakePage;

        // Если есть строка поиска, то ищем озера по имени с пагинацией
        if (name != null && !name.isEmpty()) {
            lakePage = lakeService.findLakesByNameWithPagination(name, page, pageSize);
        } else {
            lakePage = lakeService.findLakesWithPagination(page, pageSize);  // Если нет поискового запроса, показываем все озера
        }

        // Получаем количество страниц для пагинации
        int totalPages = lakePage.getTotalPages();
        // Отображение страниц пагинации
        int startPage = Math.max(0, page - 1);
        int endPage = Math.min(totalPages - 1, page + 1);

        // Передаем данные в модель для отображения
        model.addAttribute("lakePage", lakePage);  // Результаты поиска или все озера
        model.addAttribute("currentPage", page);  // Текущая страница
        model.addAttribute("totalPages", totalPages);  // Общее количество страниц
        model.addAttribute("startPage", startPage);  // Начальная страница для пагинации
        model.addAttribute("endPage", endPage);  // Конечная страница для пагинации
        model.addAttribute("name", name);  // Строка поиска для отображения в поле поиска

        return "main/main";  // Возвращаем имя представления
    }
    
    @GetMapping("/lake_page/{id}")
    public String showLakeDetails(@PathVariable("id") Long id, Model model) {
        // Загружаем озеро по id
        Optional<Lake> lakeOptional = lakeService.findLakeById(id);
        
        if (lakeOptional.isPresent()) {
            Lake lake = lakeOptional.get();
            model.addAttribute("lake", lake);
            return "card/card"; // Имя представления для отображения информации об озере
        } else {
            return "error"; // Если озеро не найдено
        }
    }


    @GetMapping()
    public List<Lake> getAllLakes() {
        return lakeService.findAll();
    }

   
}
