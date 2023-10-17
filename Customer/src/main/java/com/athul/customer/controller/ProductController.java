package com.athul.customer.controller;

import com.athul.library.dto.CategoryDto;
import com.athul.library.dto.ProductDto;
import com.athul.library.model.Category;
import com.athul.library.model.Product;
import com.athul.library.service.CategoryService;
import com.athul.library.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
public class ProductController {

    private ProductService productService;

    private CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/products")
    public String products(Model model){
        List<CategoryDto> categoryDtoList=categoryService.getCategoryAndProduct();
        List<Product> products=productService.getAllProduct();
        List<ProductDto> listViewProducts= productService.listViewProduct();
        model.addAttribute("viewProducts",listViewProducts);
        model.addAttribute("products",products);

        return "shop";
    }

    @GetMapping("/shop-grid-right")
    public String shopGrid(Model model){
        List<Product> products=productService.getAllProduct();
        List<ProductDto> listViewProducts=productService.listViewProduct();
        model.addAttribute("viewProducts",listViewProducts);
        model.addAttribute("products",products);
        return "shop-grid-right";
    }

//    @GetMapping("/shop-list-right")
//    public String shopList(Model model){
//        List<Product> products=productService.getAllProduct();
//        List<ProductDto> listViewProducts=productService.listViewProduct();
//        model.addAttribute("size",listViewProducts.size());
//        List<Category> categories=categoryService.findAllByActivated();
//        model.addAttribute("categories",categories);
//        model.addAttribute("products",listViewProducts);
//        return "shop-list-right";
//    }

    /*With Pagination Start*/

    @GetMapping("/shop-list-right")
    public String shopPage(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "5") int pageSize,
                           Model model, HttpServletRequest httpServletRequest) {
        Page<Product> productPage = productService.getProductsPaginated(page, pageSize);
        List<ProductDto> listViewProducts=productService.listViewProduct();
        model.addAttribute("size",listViewProducts.size());
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("currentPage", page);

        List<Category> categories=categoryService.findAllByActivated();
        model.addAttribute("categories",categories);

        /*For the name*/
        HttpSession httpSession1= httpServletRequest.getSession();
        String name=null;
        if(httpSession1!=null) {
            name = httpServletRequest.getRemoteUser();
        }
        model.addAttribute("name",name);

        return "shop-list-right3";
    }

    /*With Pagination End*/

    @GetMapping("/low-price")
    public String filterLowPrice(Model model){
        List<Category> categories=categoryService.findAllByActivated();
        List<Product> products= productService.filterLowPrice();
        model.addAttribute("products",products);
        model.addAttribute("categories",categories);
        model.addAttribute("size",products.size());

        return "filter-low-price";
    }

    @GetMapping("/high-price")
    public String filterHighPrice(Model model){
        List<Category> categories=categoryService.findAllByActivated();
        List<Product> products= productService.filterHighPrice();
        model.addAttribute("products",products);
        model.addAttribute("categories",categories);
        model.addAttribute("size",products.size());
        return "filter-high-price";
    }

    @GetMapping("/shop-product-right/{id}")
    public String findProduct(@PathVariable("id") Long id, Model model){
        Product product=productService.getProductById(id);
        System.out.println(product);
        Long categoryId=product.getCategory().getId();
        model.addAttribute("product",product);
        model.addAttribute("category",categoryId);
        return "shop-product-right";
    }
}
