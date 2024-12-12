// UserRepository.java
package com.example.lake_catalog.repository;

import com.example.lake_catalog.model.Lake;
import com.example.lake_catalog.model.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends Neo4jRepository<User, Long> {
    Optional<User> findByEmail(String email); // для поиска пользователя по email
    @Query("MATCH (r:Review)-[:POSTED_BY]->(u:User) WHERE id(r) = $reviewId RETURN u")
    User findUserByReviewId(Long reviewId);
    Optional<User> findById(Long id);
    // Проверка, есть ли озеро в списке "хочу посетить"
    @Query("MATCH (u:User)-[:WANT_VISIT]->(l:Lake) WHERE ID(u) = $userId AND ID(l) = $lakeId RETURN COUNT(l) > 0")
    boolean existsByIdAndWantVisitLakesContains(Long userId, Long lakeId);

    // Проверка, есть ли озеро в списке "уже посетил"
    @Query("MATCH (u:User)-[:VISITED]->(l:Lake) WHERE ID(u) = $userId AND ID(l) = $lakeId RETURN COUNT(l) > 0")
    boolean existsByIdAndVisitedLakesContains(Long userId, Long lakeId);

    // Удаление связи "хочу посетить"
    @Query("MATCH (u:User)-[r:WANT_VISIT]->(l:Lake) WHERE ID(u) = $userId AND ID(l) = $lakeId DELETE r")
    void removeWantVisitLake(Long userId, Long lakeId);

    // Удаление связи "уже посетил"
    @Query("MATCH (u:User)-[r:VISITED]->(l:Lake) WHERE ID(u) = $userId AND ID(l) = $lakeId DELETE r")
    void removeVisitedLake(Long userId, Long lakeId);

    @Query("MATCH (u:User)-[:WANT_VISIT]->(l:Lake) WHERE ID(u) = $userId RETURN l")
    List<Lake> findWantVisitLakes(Long userId);

    @Query("MATCH (u:User)-[:VISITED]->(l:Lake) WHERE ID(u) = $userId RETURN l")
    List<Lake> findVisitedLakes(Long userId);

    


}
