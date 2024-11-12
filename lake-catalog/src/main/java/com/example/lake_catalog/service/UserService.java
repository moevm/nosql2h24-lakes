package com.example.lake_catalog.service;

import com.example.lake_catalog.model.User;
import com.example.lake_catalog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public Optional<User> findUserByEmail(String email){
        return userRepository.findByEmail(email);
    }
}
