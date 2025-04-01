package com.example.WebsiteGiaDung.repository;

import com.example.WebsiteGiaDung.model.Rating;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    @Query("SELECT r FROM Rating r WHERE r.product.idSanPham = :productId")
    List<Rating> findAllByProductId(@Param("productId") Long productId);

    @Query("SELECT r FROM Rating r WHERE r.product.idSanPham = :productId AND r.user.id = :userId")
    Optional<Rating> findByProductIdAndUserId(@Param("productId") Long productId, @Param("userId") Long userId);

    @Query(value = "SELECT COUNT(*) FROM ratings WHERE product_id = :productId", nativeQuery = true)
    int countRatingsByProductId(@Param("productId") Long productId);

}
