package com.example.WebsiteGiaDung.controller;

import com.example.WebsiteGiaDung.model.LoaiSanPham;
import com.example.WebsiteGiaDung.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public String listCate(Model model) {
        List<LoaiSanPham> loaiSanPhams = categoryService.getAllCategory();
        model.addAttribute("loaiSanPhams", loaiSanPhams);
        return "categories/category-admin-list";
    }

    // New endpoint for fetching categories as JSON
    @GetMapping("/api/categories")
    @ResponseBody
    public List<LoaiSanPham> getCategoriesForDropdown() {
        return categoryService.getAllCategory();
    }

    @GetMapping("/categories/add")
    public String showAddForm(Model model) {
        model.addAttribute("category", new LoaiSanPham());
        return "/categories/add-category";
    }
    @PostMapping("/categories/add")
    public String addCategory(@Valid LoaiSanPham category, BindingResult result) {
        if (result.hasErrors()) {
            return "/categories/add-category";
        }
        categoryService.addCategory(category);
        return "redirect:/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        LoaiSanPham category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:"
                        + id));
        model.addAttribute("category", category);
        return "/categories/update-category";
    }
    // POST request to update category
    @PostMapping("/categories/update/{id}")
    public String updateCategory(@PathVariable("id") Long id, @Valid LoaiSanPham category,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            category.setIdLoaiSanPham(id);
            return "/categories/update-category";
        }
        categoryService.updateCategory(category);
        model.addAttribute("categories", categoryService.getAllCategory());
        return "redirect:/categories";
    }
    // GET request for deleting category
    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, Model model) {
        LoaiSanPham category = categoryService.getCategoryById(id).orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));
        categoryService.deleteCategoryById(id);
        model.addAttribute("categories", categoryService.getAllCategory());
        return "redirect:/categories";
    }

}
