package com.example.WebsiteGiaDung.controller;

import com.example.WebsiteGiaDung.model.Brand;
import com.example.WebsiteGiaDung.model.LoaiSanPham;
import com.example.WebsiteGiaDung.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping("/brands")
    public String listBrands(Model model) {
        List<Brand> brands = brandService.getAllBrands();
        model.addAttribute("brands", brands);
        return "brand/brand-admin-list";
    }

    @GetMapping("/api/brands")
    @ResponseBody
    public List<Brand> getBrandsForDropdown() {
        return brandService.getAllBrands();
    }

    @GetMapping("/brands/add")
    public String showAddForm(Model model) {
        model.addAttribute("brand", new Brand());
        return "/brand/add-brand";
    }

    @PostMapping("/brands/add")
    public String addBrand(@Valid Brand brand, BindingResult result) {
        if (result.hasErrors()) {
            return "/brand/add-brand";
        }
        brandService.addBrand(brand);
        return "redirect:/brands";
    }

    @GetMapping("/brands/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Brand brand = brandService.getBrandById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid brand Id:" + id));
        model.addAttribute("brand", brand);
        return "/brand/update-brand";
    }

    @PostMapping("/brands/update/{id}")
    public String updateBrand(@PathVariable("id") Long id, @Valid Brand brand,
                              BindingResult result) {
        if (result.hasErrors()) {
            brand.setIdBrand(id);
            return "/brand/update-brand";
        }
        brandService.updateBrand(brand);
        return "redirect:/brands";
    }

    @GetMapping("/brands/delete/{id}")
    public String deleteBrand(@PathVariable("id") Long id) {
        brandService.deleteBrandById(id);
        return "redirect:/brands";
    }
}
