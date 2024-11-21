package com.example.lake_catalog.service;
import com.example.lake_catalog.repository.*;
import com.example.lake_catalog.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private LakeRepository lakeRepository;

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);


    public Review addReview(Long lakeId, Long userId, String text, Integer rating) {
        // Получаем озеро и пользователя
        Lake lake = lakeRepository.findById(lakeId).orElseThrow(() -> new RuntimeException("Lake not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        logger.info("user", user.getEmail());
        logger.info("lake", lake.getName());
        // Создаем новый отзыв
        Review review = new Review();
        review.setLake(lake);
        review.setUser(user);
        review.setStars(rating);
        review.setMessage(text);
        review.setDate(LocalDate.now());

        return reviewRepository.save(review);
        //updateLakeRating(lake);
    }

    public List<Review> getReviewsForLake(Long lakeId) {
        // Получаем все отзывы для озера
        List<Review> reviews = reviewRepository.findByLakeId(lakeId);
        
        // Для каждого отзыва получаем пользователя и связываем с отзывом
        for (Review review : reviews) {
            User user = userRepository.findUserByReviewId(review.getId());
            review.setUser(user);
        }
        
        return reviews;
    }  

    
  
    // private void updateLakeRating(Lake lake) {
    //     double averageRating = lake.getReviews().stream()
    //             .mapToInt(Review::getStars)
    //             .average()
    //             .orElse(0);

    //     lake.setRating(averageRating);
    //     lakeRepository.save(lake);
    // }
}
