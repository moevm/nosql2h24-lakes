// LakeService.java
package com.example.lake_catalog.service;

import com.example.lake_catalog.model.Lake;
import com.example.lake_catalog.repository.LakeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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
            JsonNode lakesArray = objectMapper.readTree(new File("lakes_list"));
            for (JsonNode lakeJson : lakesArray) {
                String name = lakeJson.path("tags").path("name").asText(null);
                if (name == null || name.isEmpty()) continue;  // Пропускаем озера без имени
    
                String region = lakeJson.path("tags").path("region").asText("Не указан");
                String city = lakeJson.path("tags").path("city").asText("Не указан");
                double rating = lakeJson.path("tags").path("rating").asDouble(0);
                double depth = lakeJson.path("tags").path("depth").asDouble(0);
                double square = lakeJson.path("tags").path("square").asDouble(0);
    
                // Извлечение списка фотографий
                List<String> photos = new ArrayList<>();
                JsonNode photosNode = lakeJson.path("tags").path("photos");
                if (photosNode.isArray()) {
                    for (JsonNode photo : photosNode) {
                        photos.add(photo.asText());
                    }
                }
    
                String description = lakeJson.path("tags").path("description").asText("Описание отсутствует");
    
                // Проверка наличия озера в базе данных
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
                    lakeRepository.save(lake);  // Обновление существующего озера
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
                    lakeRepository.save(newLake);
                    System.out.println("Инициализация озера: " + name);

                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения файла lakes_list: " + e.getMessage(), e);
        }
    }

    public Optional<Lake> findLakeById(Long id) {
        return lakeRepository.findById(id);
    }
    

    public void addLake(Lake lake) {
        lakeRepository.save(lake);
    }
}
