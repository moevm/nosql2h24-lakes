// LakeRepository.java
package com.example.lake_catalog.repository;

import com.example.lake_catalog.model.Lake;

import org.springframework.data.domain.Page;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;

public interface LakeRepository extends Neo4jRepository<Lake, Long> {
    List<Lake> findByRegionAndCityAndNameAndRating(String region, String city, String name, Integer rating);
    Page<Lake> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Optional<Lake> findByName(String name);   
    Optional<Lake> findById(Long id);

}

