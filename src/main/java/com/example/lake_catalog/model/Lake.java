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
    private Double rating;
    private Double depth;
    private Double square;
    private List<String> photos;
    private String description;

    // Конструктор
    public Lake(Long id, String name, String region, String city, Double rating, Double depth, Double square, List<String> photos, String description) {
        this.id = id;
        this.name = name;
        this.region = region;
        this.city = city;
        this.rating = rating;
        this.depth = depth;
        this.square = square;
        this.photos = photos != null ? photos : new ArrayList<>();
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
        return (photos != null && !photos.isEmpty()) ? photos.get(0) : "https://avatars.mds.yandex.net/i?id=5b8ada33bb7301c22680812d7cd9feab_l-11548596-images-thumbs&n=13";
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

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Double getDepth() {
        return depth;
    }

    public void setDepth(Double depth) {
        this.depth = depth;
    }

    public Double getSquare() {
        return square;
    }

    public void setSquare(Double square) {
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
