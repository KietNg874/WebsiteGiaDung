package com.example.WebsiteGiaDung.repository;

import com.example.WebsiteGiaDung.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
}
