package com.example.WebsiteGiaDung.repository;

import com.example.WebsiteGiaDung.model.LoaiSanPham;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<LoaiSanPham, Long> {

}
