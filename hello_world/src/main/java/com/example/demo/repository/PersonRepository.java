package com.example.demo.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import com.example.demo.model.Person;

public interface PersonRepository extends Neo4jRepository<Person, String> {
}
