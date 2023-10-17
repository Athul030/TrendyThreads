package com.athul.customer.controller;

import com.athul.library.exception.DuplicateWishlistNameException;
import com.athul.library.model.Customer;
import com.athul.library.model.Wishlist;
import com.athul.library.service.CustomerService;
import com.athul.library.service.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class WishlistController {

    private WishlistService wishlistService;

    private CustomerService customerService;

    public WishlistController(WishlistService wishlistService, CustomerService customerService) {
        this.wishlistService = wishlistService;
        this.customerService = customerService;
    }


    @GetMapping("/wishlist")
    public String getWishList(Principal principal, Model model){
        if(principal==null){
            return "redirect:/login";
        }
        Customer customer=customerService.findByUsername(principal.getName());
        List<Wishlist> wishlists=wishlistService.findAllByCustomer(customer);
        if (wishlists.isEmpty()) {
            model.addAttribute("check","You don't have any items in your WishList");
        }
        model.addAttribute("wishlists",wishlists);
        return "wishlist";
    }



    @PostMapping("/createWishlist")
    public String createWishlist(@RequestParam("wishlistName") String wishlistName, Principal principal,RedirectAttributes attributes) {

        if (principal == null) {
            return "redirect:/login";
        } else {
            Customer customer = customerService.findByUsername(principal.getName());

            try {
                wishlistService.createWishlist(wishlistName, customer);
            }catch (DuplicateWishlistNameException e){
                attributes.addFlashAttribute("errorMessage", e.getMessage());
            }
        }

        return "redirect:/wishlist"; // Redirect to the homepage or wishlist page
    }


    @GetMapping("/add-wishlist/{id}")
    public String addToWishlist(Principal principal, @PathVariable("id") long id, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        if (principal == null) {
            return "redirect:/login";
        }
        Customer customer = customerService.findByUsername(principal.getName());
        Wishlist wishlist = wishlistService.findById(id);
        boolean exists = wishlistService.findByProductId(id, customer);

        if (exists) {
            redirectAttributes.addFlashAttribute("error", "Product already exists in wishlist");
            return "redirect:" + request.getHeader("Referer");
        }

        wishlistService.save(id,customer);
        return "redirect:/wishlist";
    }

    @GetMapping("/delete-wishlist/{id}")
    public String delete(@PathVariable("id")long wishlistId, RedirectAttributes redirectAttributes){
        wishlistService.deleteWishlist(wishlistId);
        redirectAttributes.addFlashAttribute("success","Removed Successfully");
        return "redirect:/wishlist";
    }

}
