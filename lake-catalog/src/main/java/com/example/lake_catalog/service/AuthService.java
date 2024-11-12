package com.example.lake_catalog.service;

import com.example.lake_catalog.model.User;
import com.example.lake_catalog.repository.LakeRepository;
import com.example.lake_catalog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserService userService;
    private User currentUser;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser){
        this.currentUser = currentUser;
    }

    @Autowired
    public AuthService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public void registerUser(String nickname, String email, String password) {
        if (userService.findUserByEmail(email).isPresent())
            throw new RuntimeException("Пользователь с такой почтой уже существует!");
        User user = new User();
        user.setEmail(email);
        user.setNickname(nickname);
        user.setPassword(password);
        userRepository.save(user);
        setCurrentUser(userService.findUserByEmail(email).get());
    }

    public void loginUser(String email, String password){
        System.out.println(email);
        System.out.println(password);
        Optional<User> optionalUser = userService.findUserByEmail(email);
        if (optionalUser.isEmpty())
            throw new RuntimeException("Пользователя с такой почтой не существует!");
        User user = optionalUser.get();
        if (!user.getPassword().equals(password)){
            throw new RuntimeException("Неверный пароль!");
        }
        setCurrentUser(user);
    }
}
