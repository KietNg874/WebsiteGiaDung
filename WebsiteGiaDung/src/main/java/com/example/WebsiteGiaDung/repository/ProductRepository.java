package com.example.WebsiteGiaDung.repository;

import com.example.WebsiteGiaDung.model.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<SanPham, Long> {
    // Search for products by name across all categories
    @Query("SELECT sp FROM SanPham sp WHERE sp.TenSanPham LIKE %:TenSanPham%")
    List<SanPham> findByTenSanPhamContainingIgnoreCase(@Param("TenSanPham") String TenSanPham);

    // Search for products by name within a specific category
    @Query("SELECT sp FROM SanPham sp WHERE sp.TenSanPham LIKE %:TenSanPham% AND sp.loaiSanPham.idLoaiSanPham = :categoryId")
    List<SanPham> findByTenSanPhamContainingIgnoreCaseAndLoaiSanPhamId(
            @Param("TenSanPham") String TenSanPham,
            @Param("categoryId") Long categoryId);
}
