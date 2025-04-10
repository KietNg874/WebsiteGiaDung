package com.example.WebsiteGiaDung.service;

import com.example.WebsiteGiaDung.model.SanPham;
import com.example.WebsiteGiaDung.repository.OrderDetailRepository;
import com.example.WebsiteGiaDung.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    private final OrderDetailRepository orderDetailRepository;

    public List<SanPham> getAllSanPham() {
        return productRepository.findAll();
    }

    public Optional<SanPham> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public SanPham addProduct(SanPham product) {
        return productRepository.save(product);
    }
    // Update an existing product
    public SanPham updateProduct(@NotNull SanPham product) {
        SanPham existingProduct = productRepository.findById(product.getIdSanPham()).orElseThrow(() -> new IllegalStateException("Product with ID " + product.getIdSanPham() + " does not exist."));
        existingProduct.setTenSanPham(product.getTenSanPham());
        existingProduct.setGia(product.getGia());
        existingProduct.setMoTa(product.getMoTa());
        existingProduct.setLoaiSanPham(product.getLoaiSanPham());

    // Update image paths if new ones are provided
        if (product.getImg1Path() != null && !product.getImg1Path().isEmpty()) {
            existingProduct.setImg1Path(product.getImg1Path());
        }
        if (product.getImg2Path() != null && !product.getImg2Path().isEmpty()) {
            existingProduct.setImg2Path(product.getImg2Path());
        }
        if (product.getImg3Path() != null && !product.getImg3Path().isEmpty()) {
            existingProduct.setImg3Path(product.getImg3Path());
        }
        if (product.getImg4Path() != null && !product.getImg4Path().isEmpty()) {
            existingProduct.setImg4Path(product.getImg4Path());
        }
        if (product.getImg5Path() != null && !product.getImg5Path().isEmpty()) {
            existingProduct.setImg5Path(product.getImg5Path());
        }

        // Update the product's quantity (quality) if provided
        if (product.getQuality() != null) {
            existingProduct.setQuality(product.getQuality());
        }

        return productRepository.save(existingProduct);
    }
    // Delete a product by its id
    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalStateException("Product with ID " + id + " does not exist.");
        }
        productRepository.deleteById(id);
    }

    // Search across all categories
    public List<SanPham> searchAll(String query) {
        return productRepository.findByTenSanPhamContainingIgnoreCase(query);
    }

    // Search within a specific category
    public List<SanPham> searchByCategory(String query, Long categoryId) {
        return productRepository.findByTenSanPhamContainingIgnoreCaseAndLoaiSanPhamId(query, categoryId);
    }


    public List<SanPham> getPopularProducts() {
        return orderDetailRepository.findPopularProducts();
    }
}
