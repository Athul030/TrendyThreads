package com.athul.admin.controller;

import com.athul.library.dto.CouponDto;
import com.athul.library.dto.ProductDto;
import com.athul.library.model.Category;
import com.athul.library.service.CategoryService;
import com.athul.library.service.CouponService;
import com.athul.library.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class CouponController {

    private ProductService productService;

    private CategoryService categoryService;

    private CouponService couponService;

    public CouponController(ProductService productService, CategoryService categoryService, CouponService couponService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.couponService = couponService;
    }

    @GetMapping("/coupons")
    public String getCoupon(Principal principal, Model model){
        if(principal==null){
            return "redirect:/login";
        }
        List<CouponDto> couponDtoList=couponService.getAllCoupons();
        model.addAttribute("coupons",couponDtoList);
        model.addAttribute("size",couponDtoList.size());

        return "coupons";
    }

    @GetMapping("/coupons/add-coupon")
    public String getAddCoupon(Principal principal, Model model){
        if(principal==null){
            return "redirect:/login";
        }

        List<ProductDto> productList = productService.findAllProducts();
        List<Category> categoryList = categoryService.findAllByActivatedTrue();

        LocalDate minimumDate=LocalDate.now().plusDays(1);
        model.addAttribute("minimumDate",minimumDate);
        model.addAttribute("products",productList);
        model.addAttribute("categories",categoryList);
        model.addAttribute("coupon",new CouponDto());

        return "add-coupon";
    }

    @PostMapping("/coupons/save")
    public String addCoupon(@Valid @ModelAttribute("coupon")CouponDto coupon, RedirectAttributes redirectAttributes, BindingResult result, Model model){

        if (result.hasErrors()) {

            model.addAttribute("coupon",coupon);
            return "add-coupon";
        }
        try{
            couponService.save(coupon);
            redirectAttributes.addFlashAttribute("success", "Added new Coupon successfully!");
        }catch (Exception e){
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to add new Coupon!");
        }

        return "redirect:/coupons";
    }

    /* coupons saved*/

    @GetMapping("/coupons/update-coupon/{id}")
    public String updateCouponForm(@PathVariable("id") long id, Model model, Principal principal){

        if(principal==null){
            return "redirect:/login";
        }

        CouponDto couponDto=couponService.findById(id);
        model.addAttribute("coupon",couponDto);
        LocalDate minimumDate=LocalDate.now().plusDays(1);
        model.addAttribute("minimumDate",minimumDate);
        return "update-coupon";
    }


    @PostMapping("/coupons/update-coupon/{id}")
    public String updateCoupon(@Valid @ModelAttribute("coupon") CouponDto couponDto,
                               RedirectAttributes redirectAttributes, Model model, BindingResult result){

        if (result.hasErrors()) {

            model.addAttribute("coupon", couponDto);
            return "update-coupon";
        }

        try {
            couponService.update(couponDto);

            redirectAttributes.addFlashAttribute("success", "Updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error server, please try again!");
        }
        return "redirect:/coupons";
    }

    @GetMapping("/disable-coupon/{id}")
    public String disable(@PathVariable("id")long id,RedirectAttributes redirectAttributes){

        couponService.disable(id);
        redirectAttributes.addFlashAttribute("success","Coupon Disabled");
        return "redirect:/coupons";
    }

    @GetMapping("/enable-coupon/{id}")
    public String enable(@PathVariable("id")long id, RedirectAttributes redirectAttributes){


        couponService.enable(id);
        redirectAttributes.addFlashAttribute("success","Coupon Enabled");
        return "redirect:/coupons";
    }

    @GetMapping("/delete-coupon/{id}")
    public String delete(@PathVariable("id")long id,RedirectAttributes redirectAttributes){

        couponService.deleteCoupon(id);
        redirectAttributes.addFlashAttribute("success","Coupon deleted");
        return "redirect:/coupons";
    }


}
