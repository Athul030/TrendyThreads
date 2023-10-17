package com.athul.customer.controller;


import com.athul.library.dto.ProductDto;
import com.athul.library.model.Category;
import com.athul.library.model.Product;
import com.athul.library.service.CategoryService;
import com.athul.library.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private ProductService productService;
    private CategoryService categoryService;

    private HttpSession httpSession;

    @Autowired
    public HomeController(ProductService productService, CategoryService categoryService, HttpSession httpSession) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.httpSession = httpSession;
    }

    @GetMapping(value={"/index","/"})
    public String home(Model model,HttpServletRequest httpServletRequest){
        List<Category> categories=categoryService.findAllByActivated();
        List<ProductDto> productDto=productService.listViewProduct();
        HttpSession httpSession1= httpServletRequest.getSession();
        String name=null;
        if(httpSession1!=null) {
            name = httpServletRequest.getRemoteUser();
        }
        model.addAttribute("name",name);
        model.addAttribute("categories",categories);
        model.addAttribute("products",productDto);
        return "index";
    }


    @GetMapping("/search")
    public String search(@RequestParam("keyword") String keyword, Model model){
        System.out.println("1");
        String keyword1=keyword.toLowerCase();
        List<ProductDto> productDto = productService.searchProductsUserSide(keyword1);
        model.addAttribute("size",productDto.size());
        model.addAttribute("products",productDto);
        List<Category> categories=categoryService.findAllByActivated();
        model.addAttribute("categories",categories);
        return "search-results";
    }



}
