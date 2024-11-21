package com.example.lake_catalog.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

@Node
public class Review {
    @Id
    @GeneratedValue
    private Long id;
    private Integer stars;
    private String message;
    private List<String> photos;
    private LocalDate date;
    @Relationship(type = "ABOUT", direction = Relationship.Direction.OUTGOING)
    private Lake lake;
    @Relationship(type = "POSTED_BY", direction = Relationship.Direction.OUTGOING)
    private User user;

    // Конструктор
    public Review(Long id, int stars, String message, List<String> photos, LocalDate date) {
        this.id = id;
        this.stars = stars;
        this.message = message;
        this.photos = photos;
        this.date = date;
    }

    public Review(){

    }
    
    // Геттеры и сеттеры
    public Lake getLake() {
        return lake;
    }
    
    public void setLake(Lake lake) {
        this.lake = lake;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    // Метод для вывода информации о обзоре
    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", stars=" + stars +
                ", message='" + message + '\'' +
                ", photos=" + photos +
                ", date=" + date +
                '}';
    }
}

