package com.athul.customer.controller;

import com.athul.library.dto.ProductDto;
import com.athul.library.model.Category;
import com.athul.library.model.Product;
import com.athul.library.service.CategoryService;
import com.athul.library.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@Controller
public class CategoryController {

    private ProductService productService;

    private CategoryService categoryService;

    public CategoryController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/categoryWise/{id}")
    public String categoryWise(@PathVariable("id") Long id, Model model){
        Category category=categoryService.findById(id);
        List<ProductDto> productDto=productService.findByCategory(category);
        model.addAttribute("products",productDto);

        List<Category> categories=categoryService.findAllByActivated();
        model.addAttribute("categories",categories);

        int size= productDto.size();
        model.addAttribute("size",size);
        return "shop-list-right-categoryWise";
    }
}
