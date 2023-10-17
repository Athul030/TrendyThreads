package com.athul.admin.controller;

import com.athul.library.dto.ProductDto;
import com.athul.library.model.Category;
import com.athul.library.model.Product;
import com.athul.library.service.CategoryService;
import com.athul.library.service.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class ProductController {


    private final ProductService productService;
    private final CategoryService categoryService;

    @Value("${image.storage.directory")
    private String imageStorageDirectory;

    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }




    @GetMapping("/products")
    public String products(Model model , Principal principal){
        if(principal==null){
            return "redirect:/login";
        }

        List<ProductDto> productDtoList=productService.findAll();
        model.addAttribute("title", "Manage Product");
        model.addAttribute("products", productDtoList);
        model.addAttribute("size",productDtoList.size());
        return "products";
    }

//    @GetMapping("/products/{pageNo}")
//    @Transactional
//    public String productsPage(@PathVariable("pageNo") int pageNo,Model model, Principal principal){
//        if(principal==null){
//            return "redirect:/login";
//        }
//
//        Page<Product> products=productService.pageProducts(pageNo);
//        System.out.println("pageNo"+pageNo);
//        System.out.println("pageNo"+products.getTotalPages());
//        model.addAttribute("title","Manage Product");
//        model.addAttribute("size",products.getSize());
//        model.addAttribute("totalPages",products.getTotalPages());
//        model.addAttribute("currentPage",pageNo);
//        System.out.println(products);
//        for(Product product:products){
//            System.out.println(product.getName());
//        }
//        model.addAttribute("productsPagination",products);
//        return "products";
//    }


    @GetMapping("/add-product")
    public String addProductForm(Model model, Principal principal, @ModelAttribute("categoryNew") Category category){
        if(principal==null){
            return "redirect:/login";
        }
        List<Category> categories= categoryService.findAllByActivated();
        model.addAttribute("categories",categories);
        model.addAttribute("product",new ProductDto());
        model.addAttribute("categoryNew", new Category());
        return "add-products";
    }



    @PostMapping("/save-product")
    public String saveProduct(@ModelAttribute("product") ProductDto productDto,
                              @RequestParam("imageProduct")List<MultipartFile> imageFiles,
                              RedirectAttributes attributes){

        try {

            productService.save(imageFiles,productDto);
            attributes.addFlashAttribute("success","Add successfully");

        }catch (DataIntegrityViolationException e){
            attributes.addFlashAttribute("duplicate","Duplicate entry not possible");
        }
        catch (Exception e){
            e.printStackTrace();
            attributes.addFlashAttribute("error","Failed to add");
        }
        return "redirect:/products";
    }

    @GetMapping("/update-products/{id}")
    @Transactional
    public String updateProduct(@PathVariable("id") Long id, Model model, Principal principal) {
        if(principal==null){
            return "redirect:/login";
        }
        List<Category> categories=categoryService.findAllByActivated();
        ProductDto productDto=productService.getById(id);
        model.addAttribute("title","Update Products");
        model.addAttribute("categories",categories);
        model.addAttribute("products",productDto);
        return "update-products";
    }



    @PostMapping("/update-product/{id}")
    @Transactional
    public String updateProduct(@ModelAttribute("productDto") ProductDto productDto,
                                @RequestParam("imageProduct") List<MultipartFile> imageFiles,
                                RedirectAttributes redirectAttributes,
                                @PathVariable("id") Long id) {
        try {

            productService.update(imageFiles, productDto,id);
            redirectAttributes.addFlashAttribute("success", "Update successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error server, please try again!");
        }
        return "redirect:/products";
    }


    @Transactional
    @RequestMapping(value="/enable-product/{id}", method={ RequestMethod.PUT,RequestMethod.GET})
    public String enabledProduct(@PathVariable("id") Long id, RedirectAttributes attributes){

        try {
            productService.enableById(id);
            attributes.addFlashAttribute("success", "Enabled successfully");
        }catch (Exception e){
            e.printStackTrace();
            attributes.addFlashAttribute("error","Failed to enable");

        }

        return "redirect:/products";
    }


    @Transactional
    @RequestMapping(value="/delete-product/{id}", method={RequestMethod.GET, RequestMethod.PUT})
    public String deletedProduct(@PathVariable("id") Long id, RedirectAttributes attributes){

        try {
            productService.deleteById(id);
            attributes.addFlashAttribute("success", "Deleted successfully");
        }catch (Exception e){
            e.printStackTrace();
            attributes.addFlashAttribute("error","Failed to delete");

        }

        return "redirect:/products";
    }

    @Transactional
    @GetMapping("/search-products/0")
    public String searchProduct(@RequestParam(value = "keyword") String keyword,
                                Model model) {
        List<ProductDto> productDto = productService.searchProducts(keyword);

            model.addAttribute("title", "Result Search Products");
            model.addAttribute("size", productDto.size());
            model.addAttribute("products", productDto);

        return "result-product";

    }


}
