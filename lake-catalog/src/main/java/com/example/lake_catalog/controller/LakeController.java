// LakeController.java
package com.example.lake_catalog.controller;

import com.example.lake_catalog.model.Lake;
import com.example.lake_catalog.repository.LakeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lakes")
public class LakeController {

    @Autowired
    private LakeRepository lakeRepository;

    @GetMapping("/search")
    public List<Lake> searchLakes(@RequestParam(required = false) String region,
                                  @RequestParam(required = false) String city,
                                  @RequestParam(required = false) String name,
                                  @RequestParam(required = false) Integer rating) {
        return lakeRepository.findByRegionAndCityAndNameAndRating(region, city, name, rating);

    }


}

