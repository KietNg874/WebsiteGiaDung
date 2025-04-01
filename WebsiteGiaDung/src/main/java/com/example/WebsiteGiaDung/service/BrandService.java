package com.example.WebsiteGiaDung.service;

import com.example.WebsiteGiaDung.model.Brand;
import com.example.WebsiteGiaDung.repository.BrandRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    /**
     * Get all brands from the database.
     * @return a list of all brands
     */
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    /**
     * Find a brand by its ID.
     * @param id the ID of the brand
     * @return an Optional containing the brand if found, or empty otherwise
     */
    public Optional<Brand> getBrandById(Long id) {
        return brandRepository.findById(id);
    }

    /**
     * Add a new brand to the database.
     * @param brand the brand to add
     */
    public void addBrand(Brand brand) {
        brandRepository.save(brand);
    }

    /**
     * Update an existing brand.
     * @param brand the brand with updated information
     */
    public void updateBrand(@NotNull Brand brand) {
        Brand existingBrand = brandRepository.findById(brand.getIdBrand())
                .orElseThrow(() -> new IllegalStateException("Brand with ID " +
                        brand.getIdBrand() + " does not exist."));
        existingBrand.setBrandName(brand.getBrandName());
        brandRepository.save(existingBrand);
    }

    /**
     * Delete a brand by its ID.
     * @param id the ID of the brand to delete
     */
    public void deleteBrandById(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new IllegalStateException("Brand with ID " + id + " does not exist.");
        }
        brandRepository.deleteById(id);
    }

}
