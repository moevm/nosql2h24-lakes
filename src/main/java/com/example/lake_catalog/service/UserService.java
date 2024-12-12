package com.example.lake_catalog.service;

import com.example.lake_catalog.model.Lake;
import com.example.lake_catalog.model.User;
import com.example.lake_catalog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;
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
    
    // public void removeWantVisitLake(User user, Lake lake) {
    //     user.getWantVisitLakes().remove(lake);
    //     userRepository.save(user);
    // }
    
    public void addVisitedLake(User user, Lake lake) {
        user.getVisitedLakes().add(lake);
        userRepository.save(user);
    }
    
    // public void removeVisitedLake(User user, Lake lake) {
    //     user.getVisitedLakes().remove(lake);
    //     userRepository.save(user);
    // }
    
    public boolean isEmailUnique(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return !user.isPresent();
    }

    public void updateUserProfile(Long userId, String newName, String newEmail) {
        
        // Ищем пользователя по id
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        // Проверяем уникальность email
        if (!newEmail.equals(user.getEmail())){
            if (!isEmailUnique(newEmail)) {
                throw new IllegalArgumentException("Этот email уже занят.");
            }
        }
        
        // Обновляем имя и email
        user.setNickname(newName);
        user.setEmail(newEmail);
        user.setEditDate(LocalDateTime.now());

        // Сохраняем изменения
        userRepository.save(user);
    }

    // Проверка, есть ли озеро в списке "хочу посетить"
    public boolean isLakeInWantVisit(Long userId, Long lakeId) {
        return userRepository.existsByIdAndWantVisitLakesContains(userId, lakeId);
    }

    // Проверка, есть ли озеро в списке "уже посетил"
    public boolean isLakeInVisited(Long userId, Long lakeId) {
        return userRepository.existsByIdAndVisitedLakesContains(userId, lakeId);
    }

    public void removeWantVisitLake(Long userId, Long lakeId) {
        userRepository.removeWantVisitLake(userId, lakeId);
    }

    public void removeVisitedLake(Long userId, Long lakeId) {
        userRepository.removeVisitedLake(userId, lakeId);
    }
 
    public List<Lake> getWantVisitLakes(Long userId) {
        List<Lake> lakes = userRepository.findWantVisitLakes(userId);
        return lakes != null ? lakes : List.of(); // Возвращаем пустой список вместо null
    }
    
    public List<Lake> getVisitedLakes(Long userId) {
        List<Lake> lakes = userRepository.findVisitedLakes(userId);
        return lakes != null ? lakes : List.of(); // Возвращаем пустой список вместо null
    }
    

}
