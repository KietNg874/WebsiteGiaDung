package com.example.WebsiteGiaDung.service;

import com.example.WebsiteGiaDung.model.LoaiSanPham;
import com.example.WebsiteGiaDung.repository.CategoryRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<LoaiSanPham> getAllCategory() {
        return categoryRepository.findAll();
    }

    public Optional<LoaiSanPham> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
    /**
     * Add a new category to the database.
     * @param category the category to add
     */
    public void addCategory(LoaiSanPham category) {
        categoryRepository.save(category);
    }
    /**
     * Update an existing category.
     * @param category the category with updated information
     */
    public void updateCategory(@NotNull LoaiSanPham category) {
        LoaiSanPham existingCategory = categoryRepository.findById(category.getIdLoaiSanPham())
                .orElseThrow(() -> new IllegalStateException("Category with ID " +
                        category.getIdLoaiSanPham() + " does not exist."));
        existingCategory.setTenLoaiSanPham(category.getTenLoaiSanPham());
        categoryRepository.save(existingCategory);
    }
    public void deleteCategoryById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalStateException("Category with ID " + id + " does not exist.");
        }
        categoryRepository.deleteById(id);
    }
}