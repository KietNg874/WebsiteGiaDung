package com.example.WebsiteGiaDung.controller;

import com.example.WebsiteGiaDung.model.SanPham;
import com.example.WebsiteGiaDung.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SearchController {

    @Autowired
    private ProductService productService; // Assume a service for product search.

    @GetMapping("/search")
    public String searchProducts(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "category", required = false) String category,
            Model model) {

        List<SanPham> products;
        if (category == null || category.isEmpty()) {
            // Search in all categories
            products = productService.searchAll(query);
        } else {
            try {
                Long categoryId = Long.parseLong(category); // Convert String to Long
                products = productService.searchByCategory(query, categoryId);
            } catch (NumberFormatException e) {
                // Handle invalid category ID gracefully
                products = List.of(); // Return an empty list or handle appropriately
            }
        }

        model.addAttribute("products", products);
        model.addAttribute("query", query);
        model.addAttribute("category", category);

        return "products/search-results"; // Name of the results template
    }
}
