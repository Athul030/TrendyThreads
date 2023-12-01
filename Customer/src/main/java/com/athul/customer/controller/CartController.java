package com.athul.customer.controller;


import com.athul.library.dto.ProductDto;
import com.athul.library.exception.InsufficientQuantityException;
import com.athul.library.model.Customer;
import com.athul.library.model.ShoppingCart;
import com.athul.library.service.CustomerService;
import com.athul.library.service.ProductService;
import com.athul.library.service.ShoppingCartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class CartController {

    private final ShoppingCartService cartService;
    private final ProductService productService;
    private final CustomerService customerService;

    public CartController(ShoppingCartService cartService, ProductService productService, CustomerService customerService) {
        this.cartService = cartService;
        this.productService = productService;
        this.customerService = customerService;
    }


    @GetMapping("/cart")
    public String cart(Model model, Principal principal, HttpServletRequest httpServletRequest,RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        } else {
            Customer customer = customerService.findByUsername(principal.getName());
            ShoppingCart cart = customer.getCart();
            if (cart == null ||cart.getCartItems().isEmpty()) {

                redirectAttributes.addFlashAttribute("text","Sadly cart is empty, keep shopping");

                String referer = httpServletRequest.getHeader("referer");
                if(referer=="http://localhost:8081/user/cart"){
                    return "redirect:/shop-list-right";
                }
                else if(referer!=null) {
                    return "redirect:" + referer;
                }else{
                    System.out.println("1221");
                    return "shop-list-right";
                }

            }


            HttpSession httpSession1= httpServletRequest.getSession();
            String name=null;
            if(httpSession1!=null) {
                name = httpServletRequest.getRemoteUser();
            }
            model.addAttribute("name",name);


            if (cart != null) {
                model.addAttribute("grandTotal", cart.getTotalPrice());
            }
            model.addAttribute("shoppingCart", cart);
            model.addAttribute("title", "Cart");
            return "cart";
        }

    }

    @PostMapping("/add-to-cart")
    public String addItemToCart(@RequestParam("id") Long id,
                                @RequestParam(value = "quantity", required = false, defaultValue = "1") int quantity,
                                HttpServletRequest request,
                                Model model,
                                Principal principal,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {


        ProductDto productDto = productService.getById(id);
        if (principal == null) {
            return "redirect:/login";
        } else {
            String username = principal.getName();
            try {
                ShoppingCart shoppingCart = cartService.addItemToCart(productDto, quantity, username);
                session.setAttribute("totalItems", shoppingCart.getTotalItems());
                model.addAttribute("shoppingCart", shoppingCart);
            }catch (InsufficientQuantityException ex) {
                String errorMessage = ex.getMessage();
                String referer = request.getHeader("referer");
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:" + referer;

            }

        }
        redirectAttributes.addFlashAttribute("added", "Product added successfully");

        return "redirect:/cart";
    }




    @RequestMapping(value = "/update-cart", method = RequestMethod.POST)
    public String updateCart(@RequestParam(value = "id", required = false) Long id,
                             @RequestParam(value = "quantity", required = false) int quantity,
                             Model model,
                             Principal principal,
                             @RequestParam(value = "updateButton", required = false) Long updateItemId,
                             @RequestParam(value = "deleteButton", required = false) Long removeItemId,
                             RedirectAttributes redirectAttributes,
                             HttpServletRequest httpServletRequest) {



        if (principal == null) {
            return "redirect:/login";
        } else if (updateItemId != null && quantity >= 1) {


            ProductDto productDto = productService.getById(id);
            String username = principal.getName();
            try {
                ShoppingCart shoppingCart = cartService.updateCart(productDto, quantity, username);
                model.addAttribute("shoppingCart", shoppingCart);
            }catch (InsufficientQuantityException ex){
                String errorMessage = ex.getMessage();

                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            }
        } else if (removeItemId != null) {
            ProductDto productDto = productService.getById(id);
            String username = principal.getName();
            ShoppingCart shoppingCart = cartService.removeItemFromCart(productDto, username);
            model.addAttribute("shoppingCart", shoppingCart);
            String referer = httpServletRequest.getHeader("referer");
            String check="http://localhost:8081/user/cart";
            if(referer.equals(check) && shoppingCart.getCartItems().size()==0){

                Customer customer = customerService.findByUsername(principal.getName());
                ShoppingCart cart = customer.getCart();
                if (shoppingCart == null ||shoppingCart.getCartItems().isEmpty()) {
                    redirectAttributes.addFlashAttribute("text","Sadly cart is empty, keep shopping");
                }
                return "redirect:/shop-list-right";

            }
        }

        return "redirect:/cart";
    }

}
