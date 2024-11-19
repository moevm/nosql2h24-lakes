// ReviewRepository.java
package com.example.lake_catalog.repository;

import com.example.lake_catalog.model.Review;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface ReviewRepository extends Neo4jRepository<Review, Long> {
    @Query("MATCH (r:Review)-[:ABOUT]->(l:Lake) WHERE id(l) = $lakeId RETURN r")
    List<Review> findByLakeId(Long lakeId);

    @Query("MATCH (r:Review)-[:ABOUT]->(l:Lake), (r)-[:POSTED_BY]->(u:User) " +
       "WHERE id(l) = $lakeId " +
       "RETURN r, u")
    List<Map<String, Object>> findReviewsWithUserByLakeId(Long lakeId);

    
}
