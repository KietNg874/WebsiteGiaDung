package com.example.WebsiteGiaDung.controller;

import com.example.WebsiteGiaDung.model.Brand;
import com.example.WebsiteGiaDung.model.SanPham;
import com.example.WebsiteGiaDung.service.BrandService;
import com.example.WebsiteGiaDung.service.CategoryService;
import com.example.WebsiteGiaDung.service.ProductService;
import com.example.WebsiteGiaDung.service.RatingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private RatingService ratingService;

    @Value("${image.upload.dir}")
    private String uploadDir;

    @GetMapping("/products")
    public String listProducts(Model model) {
        List<SanPham> sanPhams = productService.getAllSanPham();
        model.addAttribute("sanPhams", sanPhams);
        return "/products/product-admin-list";
    }

    @GetMapping("/products/category")
    public String getProductsByCategory(@RequestParam("categoryId") Long categoryId,
                                        @RequestParam(value = "sort", required = false) String sort,
                                        Model model) {
        var category = categoryService.getCategoryById(categoryId);
        if (category.isPresent()) {
            var products = category.get().getSanPhams();

            // Sort products based on 'sort' parameter
            if ("asc".equalsIgnoreCase(sort)) {
                products.sort(Comparator.comparing(SanPham::getGia)); // Assuming getGia() returns the price
            } else if ("desc".equalsIgnoreCase(sort)) {
                products.sort(Comparator.comparing(SanPham::getGia).reversed());
            }

            model.addAttribute("category", category.get());
            model.addAttribute("products", products); // Pass sorted products to the view
            return "/products/category-products"; // Refers to category-products.html
        } else {
            model.addAttribute("error", "Category not found!");
            return "redirect:/products/by-category"; // Redirect to the main page if the category is invalid
        }
    }


    @GetMapping("/products/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new SanPham());
        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("branhs", brandService.getAllBrands());
        return "/products/add-product";
    }

    @PostMapping("/products/add")
    public String addProduct(@Valid SanPham product, BindingResult result,
                             @RequestParam("img1") MultipartFile img1,
                             @RequestParam("img2") MultipartFile img2,
                             @RequestParam("img3") MultipartFile img3,
                             @RequestParam("img4") MultipartFile img4,
                             @RequestParam("img5") MultipartFile img5) {
        if (result.hasErrors()) {
            return "/products/add-product";
        }

        try {
            // Save each image if provided and set the file path in the product
            if (!img1.isEmpty()) {
                product.setImg1Path(saveImage(img1));
            }
            if (!img2.isEmpty()) {
                product.setImg2Path(saveImage(img2));
            }
            if (!img3.isEmpty()) {
                product.setImg3Path(saveImage(img3));
            }
            if (!img4.isEmpty()) {
                product.setImg4Path(saveImage(img4));
            }
            if (!img5.isEmpty()) {
                product.setImg5Path(saveImage(img5));
            }

            // Set quality
            if (product.getQuality() == null) {
                product.setQuality(0L); // Default value if not provided
            }

            // Save the product
            productService.addProduct(product);
        } catch (IOException e) {
            e.printStackTrace();
            return "/products/add-product";
        }

        return "redirect:/products";
    }


    @GetMapping("/products/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        SanPham product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));

        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("brands", brandService.getAllBrands()); // Add this line

        return "/products/update-product";
    }

    @GetMapping("/products/{id}")
    public String showDetailProduct(@PathVariable Long id, Model model, HttpServletRequest request) {
        // Fetch the product
        SanPham product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));

        Long userId = 1L; // Replace with actual logged-in user ID
        int userRating = ratingService.getUserRating(id, userId);
        double averageRating = ratingService.getAverageRating(id);

        // Debugging
        System.out.println("User Rating: " + userRating);

        model.addAttribute("product", product);
        model.addAttribute("userRating", userRating);       // Pass user rating
        model.addAttribute("averageRating", averageRating); // Pass average rating
        return "/products/product-detail";
    }


    @PostMapping("/products/update/{id}")
    public String updateProduct(@PathVariable Long id,
                                @Valid SanPham product,
                                BindingResult result,
                                @RequestParam("img1") MultipartFile img1,
                                @RequestParam("img2") MultipartFile img2,
                                @RequestParam("img3") MultipartFile img3,
                                @RequestParam("img4") MultipartFile img4,
                                @RequestParam("img5") MultipartFile img5) {
        if (result.hasErrors()) {
            product.setIdSanPham(id);
            return "/products/update-product";
        }

        // Fetch the existing product
        SanPham existingProduct = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id: " + id));

        // Update other fields
        existingProduct.setTenSanPham(product.getTenSanPham());
        existingProduct.setGia(product.getGia());
        existingProduct.setMoTa(product.getMoTa());
        existingProduct.setLoaiSanPham(product.getLoaiSanPham());
        existingProduct.setBrand(product.getBrand());

        // Fix: Update the quantity field
        existingProduct.setQuality(product.getQuality()); // Allow setting quality even if it's 0

        try {
            // Update images if provided
            if (!img1.isEmpty()) existingProduct.setImg1Path(saveImage(img1));
            if (!img2.isEmpty()) existingProduct.setImg2Path(saveImage(img2));
            if (!img3.isEmpty()) existingProduct.setImg3Path(saveImage(img3));
            if (!img4.isEmpty()) existingProduct.setImg4Path(saveImage(img4));
            if (!img5.isEmpty()) existingProduct.setImg5Path(saveImage(img5));
        } catch (IOException e) {
            e.printStackTrace();
            return "/products/update-product"; // Return to form on error
        }

        // Save the updated product
        productService.updateProduct(existingProduct);

        return "redirect:/products";
    }



    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/products";
    }

    private String saveImage(MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename();  // Get only the original name
        Path filePath = Paths.get(uploadDir, originalFilename); // Save to uploads directory
        Files.createDirectories(filePath.getParent());
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return originalFilename; // Return only the filename
    }

    @GetMapping("/product-search")
    public String searchSanPham(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "category", required = false) Long category,
            Model model) {

        List<SanPham> sanPhams;
        if (category == null || category == 0) {
            // Search all categories
            sanPhams = productService.searchAll(query);
        } else {
            // Search specific category
            sanPhams = productService.searchByCategory(query, category);
        }

        model.addAttribute("sanPhams", sanPhams);
        model.addAttribute("query", query);
        model.addAttribute("category", category);

        return "products/search-results"; // Update with the correct template name
    }

    @GetMapping("/products/brand")
    public String getProductsByBrand(@RequestParam("brandId") Long brandId, Model model) {
        var brand = brandService.getBrandById(brandId);
        if (brand.isPresent()) {
            System.out.println("Brand Name: " + brand.get().getBrandName());
            brand.get().getSanPhams().forEach(product ->
                    System.out.println("Product Name: " + product.getTenSanPham())
            );
            model.addAttribute("brand", brand.get());
            return "brand/brand-category-products";
        } else {
            System.out.println("Brand not found!");
            return "redirect:/products";
        }
    }

    @GetMapping("/popular-products")
    public String showPopularProducts(Model model) {
        List<SanPham> popularProducts = productService.getPopularProducts();
        model.addAttribute("popularProducts", popularProducts);
        return "popular-product-list";
    }

}
