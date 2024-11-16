package com.example.lake_catalog.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node
public class Lake {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String region;
    private String city;
    private double rating;
    private double depth;
    private double square;
    private List<String> photos;
    private String description;

    @Relationship(type = "HAS_REVIEW", direction = Relationship.Direction.OUTGOING)
    private List<Review> reviews;

    // Конструктор
    public Lake(Long id, String name, String region, String city, double rating, double depth, double square, List<String> photos, String description) {
        this.id = id;
        this.name = name;
        this.region = region;
        this.city = city;
        this.rating = rating;
        this.depth = depth;
        this.square = square;
        this.photos = new ArrayList<>();
        this.description = description;
    }

    public Lake(){

    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrimaryPhoto() {
        return (photos != null && !photos.isEmpty()) ? photos.get(0) : "/assets/lake.jpg";
    }
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public double getSquare() {
        return square;
    }

    public void setSquare(double square) {
        this.square = square;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = (photos != null) ? photos : new ArrayList<>();
    }
    

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Метод для вывода информации об озере
    @Override
    public String toString() {
        return "Lake{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", region='" + region + '\'' +
                ", city='" + city + '\'' +
                ", rating=" + rating +
                ", depth=" + depth +
                ", square=" + square +
                ", photos=" + photos +
                ", description='" + description + '\'' +
                '}';
    }
}
