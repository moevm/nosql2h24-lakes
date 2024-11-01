// LakeRepository.java
package com.example.lake_catalog.repository;

import com.example.lake_catalog.model.Lake;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.List;

public interface LakeRepository extends Neo4jRepository<Lake, Long> {
    List<Lake> findByRegionAndCityAndNameAndRating(String region, String city, String name, Integer rating);
}
