package com.example.lake_catalog.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Node
public class User {
    @Id
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String photo;
    private LocalDate creationDate;
    private LocalDateTime editDate;

    public User(){

    }
    // Конструктор
    public User(Long id, String email, String password, String nickname, String photo, LocalDate creationDate, LocalDateTime editDate) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.photo = photo;
        this.creationDate = creationDate;
        this.editDate = editDate;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getEditDate() {
        return editDate;
    }

    public void setEditDate(LocalDateTime editDate) {
        this.editDate = editDate;
    }

    // Метод для вывода информации о пользователе
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", photo='" + photo + '\'' +
                ", creationDate=" + creationDate +
                ", editDate=" + editDate +
                '}';
    }
}

