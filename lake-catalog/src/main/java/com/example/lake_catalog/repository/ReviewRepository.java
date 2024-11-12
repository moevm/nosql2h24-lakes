// ReviewRepository.java
package com.example.lake_catalog.repository;

import com.example.lake_catalog.model.Review;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.List;

public interface ReviewRepository extends Neo4jRepository<Review, Long> {
//    List<Review> findByLakeId(Long lakeId);
}
