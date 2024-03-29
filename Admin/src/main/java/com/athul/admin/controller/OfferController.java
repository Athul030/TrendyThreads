package com.athul.admin.controller;

import com.athul.library.dto.OfferDto;
import com.athul.library.dto.ProductDto;
import com.athul.library.model.Category;
import com.athul.library.service.CategoryService;
import com.athul.library.service.OfferService;
import com.athul.library.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class OfferController {

    private OfferService offerService;
    private CategoryService categoryService;

    private ProductService productService;


    public OfferController(OfferService offerService, CategoryService categoryService, ProductService productService) {
        this.offerService = offerService;
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping("/offers")
    public String getCoupon(Principal principal, Model model){
        if(principal==null){
            return "redirect:/login";
        }
        List<OfferDto> offerDtoList=offerService.getAllOffers();
        model.addAttribute("offers",offerDtoList);
        model.addAttribute("size",offerDtoList.size());

        return "offers";
    }

    @GetMapping("/offers/add-offer")
    public String getAddOffer(Principal principal, Model model){

        if(principal == null){
            return "redirect:/login";
        }

        List<ProductDto> productList = productService.findAllProducts();
        List<Category> categoryList = categoryService.findAllByActivatedTrue();

        model.addAttribute("products",productList);
        model.addAttribute("categories",categoryList);
        model.addAttribute("offer",new OfferDto());

        return "add-offer";
    }

    @PostMapping("/offers/save")
    public String addOffer(@ModelAttribute("offer")OfferDto offer, RedirectAttributes redirectAttributes){

        try{
            offerService.save(offer);
            redirectAttributes.addFlashAttribute("success", "Added new Offer successfully!");
        }catch (Exception e){
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to add new Offer!");
        }

        return "redirect:/offers";
    }

    @GetMapping("/offers/update-offer/{id}")
    public String updateOfferForm(@PathVariable("id") long id, Model model, Principal principal){

        if(principal==null){
            return "redirect:/login";
        }
        List<ProductDto> productList = productService.findAllProducts();
        List<Category> categoryList = categoryService.findAllByActivatedTrue();

        model.addAttribute("products",productList);
        model.addAttribute("categories",categoryList);

        OfferDto offerDto=offerService.findById(id);
        model.addAttribute("offer",offerDto);
        return "update-offer";
    }


    @PostMapping("/offers/update-offer/{id}")
    public String updateCoupon(@ModelAttribute("offer") OfferDto offerDto,
                               RedirectAttributes redirectAttributes){

        try {
            offerService.update(offerDto);
            redirectAttributes.addFlashAttribute("success", "Updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error server, please try again!");
        }
        return "redirect:/offers";
    }

    @GetMapping("/disable-offer/{id}")
    public String disable(@PathVariable("id")long id,RedirectAttributes redirectAttributes){

        offerService.disable(id);
        redirectAttributes.addFlashAttribute("success","Offer Disabled");
        return "redirect:/offers";
    }

    @GetMapping("/enable-offer/{id}")
    public String enable(@PathVariable("id")long id, RedirectAttributes redirectAttributes){


        offerService.enable(id);

        redirectAttributes.addFlashAttribute("success","Offer Enabled");
        return "redirect:/offers";
    }

    @GetMapping("/delete-offer/{id}")
    public String delete(@PathVariable("id")long id,RedirectAttributes redirectAttributes){

        offerService.deleteOffer(id);

        redirectAttributes.addFlashAttribute("success","Offer deleted");

        return "redirect:/offers";
    }
}
