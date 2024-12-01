package com.example.lake_catalog.service;

import com.example.lake_catalog.model.Lake;
import com.example.lake_catalog.model.User;
import com.example.lake_catalog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

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

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public void addWantVisitLake(Long userId, Lake lake) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.getWantVisitLakes().add(lake);
            userRepository.save(user);
        }
    }

    public void addVisitedLake(Long userId, Lake lake) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.getVisitedLakes().add(lake);
            userRepository.save(user);
        }
    }

    public void addWantVisitLake(User user, Lake lake) {
        user.getWantVisitLakes().add(lake);
        userRepository.save(user);
    }
    
    public void removeWantVisitLake(User user, Lake lake) {
        user.getWantVisitLakes().remove(lake);
        userRepository.save(user);
    }
    
    public void addVisitedLake(User user, Lake lake) {
        user.getVisitedLakes().add(lake);
        userRepository.save(user);
    }
    
    public void removeVisitedLake(User user, Lake lake) {
        user.getVisitedLakes().remove(lake);
        userRepository.save(user);
    }
    

}
