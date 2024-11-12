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
import java.util.List;

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

    public Page<Lake> findLakesWithPagination(int page, int pageSize) {
        return lakeRepository.findAll(PageRequest.of(page, pageSize));
    }

    public void initialize() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode lakesArray = objectMapper.readTree(new File("lakes_list"));
            for (JsonNode lakeJson : lakesArray) {
                String name = lakeJson.path("tags").path("name").asText();
                if (lakeRepository.findByName(name).isEmpty()) {
                    Lake lake = new Lake();
                    lake.setRegion("Ленинградская область");
                    lake.setName(name);
                    lakeRepository.save(lake);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addLake(Lake lake) {
        lakeRepository.save(lake);
    }
}
