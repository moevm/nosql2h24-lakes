// UserRepository.java
package com.example.lake_catalog.repository;

import com.example.lake_catalog.model.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.Optional;

public interface UserRepository extends Neo4jRepository<User, Long> {
    Optional<User> findByEmail(String email); // для поиска пользователя по email
}
