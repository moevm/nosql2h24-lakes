// LakeController.java
package com.example.lake_catalog.controller;

import com.example.lake_catalog.model.Lake;
import com.example.lake_catalog.service.LakeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String showMainPage(Model model, @RequestParam(defaultValue = "0") int page) {
        int pageSize = 8; // Количество озёр на странице
        Page<Lake> lakePage = lakeService.findLakesWithPagination(page, pageSize);
        int totalPages = lakePage.getTotalPages();

        // Пределы видимых страниц
        int startPage = Math.max(0, page - 1);
        int endPage = Math.min(totalPages - 1, page + 1);

        model.addAttribute("lakePage", lakePage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "main/main";
    }

    @GetMapping("/search")
    public List<Lake> searchLakes(String region, String city, String name, Integer rating) {
        return lakeService.findByRegionAndCityAndNameAndRating(region, city, name, rating);
    }

    @GetMapping()
    public List<Lake> getAllLakes() {
        return lakeService.findAll();
    }

    @GetMapping("initialize")
    public ResponseEntity<Void> initialize() {
        lakeService.initialize();
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/lakes")).build();
    }
}
