package com.example.WebsiteGiaDung.controller;

import com.example.WebsiteGiaDung.model.Brand;
import com.example.WebsiteGiaDung.model.LoaiSanPham;
import com.example.WebsiteGiaDung.model.SanPham;
import com.example.WebsiteGiaDung.service.BrandService;
import com.example.WebsiteGiaDung.service.CategoryService;
import com.example.WebsiteGiaDung.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private BrandService brandService;

    @GetMapping("/")  // Root URL
    public String homePage(Model model) {
        // Load categories and products to display on the home page
        List<LoaiSanPham> loaiSanPhams = categoryService.getAllCategory();
        model.addAttribute("loaiSanPhams", loaiSanPhams);
        List<SanPham> sanPhams = productService.getAllSanPham();
        model.addAttribute("sanPhams", sanPhams);
        List<Brand> brands = brandService.getAllBrands();  // Correct variable name
        model.addAttribute("brands", brands);  // Ensure alignment with the template

        // Load popular products based on the number of orders
        List<SanPham> popularProducts = productService.getPopularProducts();
        model.addAttribute("popularProducts", popularProducts);
        return "Home/index";  // Refers to the Thymeleaf template "index.html" in the "Home" folder
    }
}