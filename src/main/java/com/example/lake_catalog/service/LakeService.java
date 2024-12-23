// LakeService.java
package com.example.lake_catalog.service;

import com.example.lake_catalog.model.Lake;
import com.example.lake_catalog.repository.LakeRepository;
import com.example.lake_catalog.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.example.lake_catalog.model.User;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Arrays;


@Service
public class LakeService {

    private final LakeRepository lakeRepository;

    @Autowired
    public LakeService(LakeRepository lakeRepository) {
        this.lakeRepository = lakeRepository;
    }

    
    public List<Lake> findByRegionAndCityAndNameAndRating(String region, String city, String name, Integer rating) {
        return lakeRepository.findByRegionAndCityAndNameAndRating(region, city, name, rating);
    }

    public List<Lake> findAll() {
        return lakeRepository.findAll();
    }

    public Page<Lake> findLakesByNameWithPagination(String name, int page, int pageSize) {
        return lakeRepository.findByNameContainingIgnoreCase(name, PageRequest.of(page, pageSize));
    }


    public Page<Lake> findLakesWithPagination(int page, int pageSize) {
        return lakeRepository.findAll(PageRequest.of(page, pageSize));
    }

    public void initialize() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Чтение JSON файла
            JsonNode lakesArray = objectMapper.readTree(new File("/app/data/records.json"));
            for (JsonNode lakeJson : lakesArray) {
                // Извлечение основных данных
                JsonNode tagsNode = lakeJson.path("l").path("properties");
                String name = tagsNode.path("name").asText(null);
                if (name == null || name.isEmpty()) {
                    System.out.println("Пропуск объекта без имени.");
                    continue; // Пропускаем объекты без имени
                }
    
                // Установка значений с учётом значений по умолчанию
                String region = tagsNode.path("region").asText("Не указан");
                String city = tagsNode.path("city").asText("Не указан");
                Double rating = tagsNode.path("rating").asDouble(0.0);
                Double depth = tagsNode.path("depth").asDouble(0.0);
                Double square = tagsNode.path("square").asDouble(0.0);
    
                // Извлечение списка фотографий с проверкой
                List<String> photos = new ArrayList<>();
                JsonNode photosNode = tagsNode.path("photos");
                if (photosNode.isArray()) {
                    for (JsonNode photo : photosNode) {
                        photos.add(photo.asText());
                    }
                } else {
                    photos.add("https://cdn1.ozone.ru/s3/multimedia-1-z/6980409107.jpg"); // Значение по умолчанию
                }
    
                String description = tagsNode.path("description").asText("Описание отсутствует");
    
                // Проверка существования озера в базе
                Optional<Lake> optionalLake = lakeRepository.findByName(name);
                if (optionalLake.isPresent()) {
                    Lake lake = optionalLake.get();
                    lake.setRegion(region);
                    lake.setCity(city);
                    lake.setRating(rating);
                    lake.setDepth(depth);
                    lake.setSquare(square);
                    lake.setPhotos(photos);
                    lake.setDescription(description);
                    lakeRepository.save(lake); // Обновление существующего озера
                    System.out.println("Обновлено озеро: " + name);
                } else {
                    Lake newLake = new Lake();
                    newLake.setName(name);
                    newLake.setRegion(region);
                    newLake.setCity(city);
                    newLake.setRating(rating);
                    newLake.setDepth(depth);
                    newLake.setSquare(square);
                    newLake.setPhotos(photos);
                    newLake.setDescription(description);
                    lakeRepository.save(newLake); // Добавление нового озера
                    System.out.println("Добавлено озеро: " + name);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения файла records.json: " + e.getMessage(), e);
        }
    }
    
    List<Lake> getAllLakes() {
        return lakeRepository.getAllLakes();
    }
    

    public Optional<Lake> findLakeById(Long id) {
        return lakeRepository.findById(id);
    }
    

    public void addLake(Lake lake) {
        lakeRepository.save(lake);
    }

    public Page<Lake> filterLakes(String name, String min_depth, String max_depth, String min_square, String max_square, String city, String region, String rating, int page, int pageSize) {
        List<Lake> lakes = lakeRepository.findAll(); // Загружаем все озера

        // Фильтруем по имени, если указано
        if (name != null && !name.isEmpty()) {
            lakes = lakes.stream()
                    .filter(lake -> lake.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Фильтруем по глубине, если указано
        if ((min_depth != null && !min_depth.isEmpty()) || (max_depth != null && !max_depth.isEmpty())) {
            Double min_depthValue = min_depth != null && !min_depth.isEmpty() ? Double.parseDouble(min_depth) : 0.0; // По умолчанию 0
            Double max_depthValue = max_depth != null && !max_depth.isEmpty() ? Double.parseDouble(max_depth) : Double.MAX_VALUE; // По умолчанию MAX_VALUE
            lakes = lakes.stream()
                    .filter(lake -> lake.getDepth() >= min_depthValue && lake.getDepth() <= max_depthValue)
                    .collect(Collectors.toList());
        }

        // Фильтруем по площади, если указано
        if ((min_square != null && !min_square.isEmpty()) || (max_square != null && !max_square.isEmpty())) {
            Double min_squareValue = min_square != null && !min_square.isEmpty() ? Double.parseDouble(min_square) : 0.0; // По умолчанию 0
            Double max_squareValue = max_square != null && !max_square.isEmpty() ? Double.parseDouble(max_square) : Double.MAX_VALUE; // По умолчанию MAX_VALUE
            lakes = lakes.stream()
                    .filter(lake -> lake.getSquare() >= min_squareValue && lake.getSquare() <= max_squareValue)
                    .collect(Collectors.toList());
        }

        if (city != null && !city.isEmpty()) {
            List<String> cities = Arrays.asList(city.split(","));
            lakes = lakes.stream()
                    .filter(lake -> lake.getCity() != null &&
                            cities.stream().anyMatch(c -> lake.getCity().contains(c)))
                    .collect(Collectors.toList());
        }

        if (region != null && !region.isEmpty()) {
            List<String> regions = Arrays.asList(region.split(","));
            lakes = lakes.stream()
                    .filter(lake -> lake.getRegion() != null &&
                            regions.stream().anyMatch(r -> lake.getRegion().contains(r)))
                    .collect(Collectors.toList());
        }

        if (rating != null && !rating.isEmpty()) {
            List<Integer> ratings = Arrays.stream(rating.split(","))
                    .map(r -> {
                        switch (r) {
                            case "withoutRating":
                                return 0;
                            case "rating1":
                                return 1;
                            case "rating2":
                                return 2;
                            case "rating3":
                                return 3;
                            case "rating4":
                                return 4;
                            case "rating5":
                                return 5;
                            default:
                                throw new IllegalArgumentException("Invalid rating: " + r);
                        }
                    })
                    .collect(Collectors.toList());
            lakes = lakes.stream()
                    .filter(lake ->
                            ratings.contains((int)Math.round(lake.getRating())))
                    .collect(Collectors.toList());
        }

        // Пагинация
        int start = page * pageSize;
        int end = Math.min(start + pageSize, lakes.size());

        // Проверяем на выход за пределы
        if (start > lakes.size()) {
            return Page.empty(); // Возвращаем пустую страницу
        }

        return new PageImpl<>(lakes.subList(start, end), PageRequest.of(page, pageSize), lakes.size());
    }
    
}
