package com.example.WebsiteGiaDung.controller;

import com.example.WebsiteGiaDung.model.RatingRequestDTO;
import com.example.WebsiteGiaDung.model.User;
import com.example.WebsiteGiaDung.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @PostMapping("/submit")
    @ResponseBody
    public ResponseEntity<?> submitRating(@RequestBody RatingRequestDTO ratingRequestDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = ((User) authentication.getPrincipal()).getId(); // Replace CustomUserDetails with your UserDetails implementation

            // Debugging
            System.out.println("Received Rating: " + ratingRequestDTO.getRating() + " for Product: " + ratingRequestDTO.getProductId());

            // Save the rating
            ratingService.saveRating(ratingRequestDTO.getProductId(), userId, ratingRequestDTO.getRating());

            return ResponseEntity.ok(Collections.singletonMap("success", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Collections.singletonMap("success", false));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Integer>> getUserRating(@RequestParam Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();

        int userRating = ratingService.getUserRating(productId, userId);

        return ResponseEntity.ok(Collections.singletonMap("userRating", userRating));
    }

    @GetMapping("/average")
    public ResponseEntity<Map<String, Double>> getAverageRating(@RequestParam Long productId) {
        double averageRating = ratingService.getAverageRating(productId);

        return ResponseEntity.ok(Collections.singletonMap("averageRating", averageRating));
    }
}

