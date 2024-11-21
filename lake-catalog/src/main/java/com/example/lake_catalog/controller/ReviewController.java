package com.example.lake_catalog.controller;

import com.example.lake_catalog.model.*;
import com.example.lake_catalog.service.*;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("lakes/lake_page/{lakeId}")
public class ReviewController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private LakeService lakeService;

    @Autowired
    private UserService userService;

    // Метод для получения списка отзывов для озера (API для получения отзывов)
    @GetMapping("/reviews")
    public ResponseEntity<?> getLakeReviews(@PathVariable Long lakeId) {
        try {
            List<Review> reviews = reviewService.getReviewsForLake(lakeId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            logger.error("error ID {}: {}", lakeId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    // Метод для добавления отзыва
    @PostMapping("/reviews")
    public ResponseEntity<?> addReview(
            @PathVariable Long lakeId,
            @RequestBody Review reviewRequest,
            HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not logged in");
        }
        Optional<Lake> lake = lakeService.findLakeById(lakeId);
        if (lake.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lake not found");
        }
        System.out.println("Current user Email: " + currentUser.getEmail());
        
        Optional<User> userOptional = userService.findUserByEmail(currentUser.getEmail());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with the given email not found");
        }

        User user = userOptional.get();
        System.out.println("Curr user: " + user);
        Review review = new Review();
        review.setLake(lake.get());
        review.setUser(user);
        review.setMessage(reviewRequest.getMessage());
        review.setStars(reviewRequest.getStars());
        review.setDate(LocalDate.now());


        try {
            Review savedReview = reviewService.addReview(lakeId, user.getId(), review.getMessage(), review.getStars());
            return ResponseEntity.ok(savedReview);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // Ответ 400 в случае ошибки
        }
    }
}
