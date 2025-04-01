package com.example.WebsiteGiaDung.service;

import com.example.WebsiteGiaDung.model.Rating;
import com.example.WebsiteGiaDung.model.SanPham;
import com.example.WebsiteGiaDung.model.User;
import com.example.WebsiteGiaDung.repository.ProductRepository;
import com.example.WebsiteGiaDung.repository.RatingRepository;
import com.example.WebsiteGiaDung.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;


    // Save or update user rating for a product
    public void saveRating(Long productId, Long userId, int ratingValue) {
        // Fetch User from the database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Fetch Product from the database
        SanPham product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        // Check if the rating already exists
        Optional<Rating> existingRating = ratingRepository.findByProductIdAndUserId(productId, userId);

        // Create or update the rating
        Rating rating = existingRating.orElse(new Rating());
        rating.setProduct(product);
        rating.setUser(user);
        rating.setRatingValue(ratingValue);

        // Save the rating
        ratingRepository.save(rating);
    }

    public int getNumberOfVotes(Long productId) {
        return ratingRepository.countRatingsByProductId(productId);
    }



    // Get the user's rating for a specific product
    public int getUserRating(Long productId, Long userId) {
        return ratingRepository.findByProductIdAndUserId(productId, userId)
                .map(Rating::getRatingValue)
                .orElse(0);
    }

    // Get the average rating for a product
    public double getAverageRating(Long productId) {
        return ratingRepository.findAllByProductId(productId)
                .stream()
                .mapToInt(Rating::getRatingValue)
                .average()
                .orElse(0.0);
    }
}
