package com.example.WebsiteGiaDung.repository;

import com.example.WebsiteGiaDung.model.Cart;
import com.example.WebsiteGiaDung.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
