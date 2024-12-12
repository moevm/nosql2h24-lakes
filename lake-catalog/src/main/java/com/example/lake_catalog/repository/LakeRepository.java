// LakeRepository.java
package com.example.lake_catalog.repository;

import com.example.lake_catalog.model.Lake;

import org.springframework.data.domain.Page;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LakeRepository extends Neo4jRepository<Lake, Long> {
    List<Lake> findByRegionAndCityAndNameAndRating(String region, String city, String name, Integer rating);
    Page<Lake> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Optional<Lake> findByName(String name);   
    Optional<Lake> findById(Long id);

    @Query("MATCH (l:Lake) RETURN DISTINCT l.region AS region")
    List<String> findAllRegions();

    // Получение всех уникальных городов
    @Query("MATCH (l:Lake) RETURN DISTINCT l.city AS city")
    List<String> findAllCities();

    @Query("MATCH (l:Lake) WHERE (l.region IN $regions OR $regions IS NULL) AND (l.city IN $cities OR $cities IS NULL) " +
           "AND (l.depth >= $minDepth AND l.depth <= $maxDepth) " +
           "AND (l.square >= $minArea AND l.square <= $maxArea) RETURN l")
    List<Lake> filterLakes(@Param("regions") List<String> regions, 
                           @Param("cities") List<String> cities, 
                           @Param("minDepth") double minDepth, 
                           @Param("maxDepth") double maxDepth, 
                           @Param("minArea") double minArea, 
                           @Param("maxArea") double maxArea);


    @Query("MATCH (l:Lake) RETURN l")
    List<Lake> getAllLakes();
                           
}

