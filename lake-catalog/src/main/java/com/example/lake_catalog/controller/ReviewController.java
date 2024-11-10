package com.example.lake_catalog.controller;

import com.example.lake_catalog.model.Review;
import com.example.lake_catalog.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/lake/{lakeId}")
    public List<Review> getReviewsByLake(@PathVariable Long lakeId) {
        return reviewRepository.findByLakeId(lakeId);
    }

    @PostMapping("/add")
    public Review addReview(@RequestBody Review newReview) {
        return reviewRepository.save(newReview);
    }
}
