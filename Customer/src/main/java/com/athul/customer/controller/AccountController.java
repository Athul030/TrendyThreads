package com.athul.customer.controller;

import com.athul.library.dto.AddressDto;
import com.athul.library.dto.CustomerDto;
import com.athul.library.model.Address;
import com.athul.library.model.Category;
import com.athul.library.model.Customer;
import com.athul.library.model.Order;
import com.athul.library.repository.CustomerRepository;
import com.athul.library.repository.OrderRepository;
import com.athul.library.service.AddressService;
import com.athul.library.service.CategoryService;
import com.athul.library.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class AccountController {

    private final CustomerService customerService;

    private final CustomerRepository customerRepository;

    private final AddressService addressService;

    private final OrderRepository orderRepository;

    private final CategoryService categoryService;

    public AccountController(CustomerService customerService, CustomerRepository customerRepository, AddressService addressService, OrderRepository orderRepository, CategoryService categoryService) {
        this.customerService = customerService;
        this.customerRepository = customerRepository;
        this.addressService = addressService;
        this.orderRepository = orderRepository;
        this.categoryService = categoryService;
    }

    @GetMapping("/account")
    public String accountHome(Model model, Principal principal, HttpServletRequest httpServletRequest) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);

        List<Order> orders = orderRepository.findByCustomer(customer);
        Collections.sort(orders, Collections.reverseOrder(Comparator.comparing(Order::getId)));
        model.addAttribute("orders", orders);

        model.addAttribute("customer", customer);
        model.addAttribute("addresses", customer.getAddress());

        /*For the name*/
        HttpSession httpSession1 = httpServletRequest.getSession();
        String name = null;
        if (httpSession1 != null) {
            name = httpServletRequest.getRemoteUser();
        }
        model.addAttribute("name", name);

        return "page-account";
    }

    @GetMapping("/add-address")
    public String addAddress(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        AddressDto addressDto = new AddressDto();
        model.addAttribute("addressDto", addressDto);
        return "add-address";
    }

    @PostMapping("/save-address")
    public String saveAddress(Model model, Principal principal, @ModelAttribute("addressDto") AddressDto addressDto, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        Address newAddress = new Address();
        newAddress = addressService.save(addressDto, username);
        model.addAttribute("address", newAddress);
        redirectAttributes.addFlashAttribute("message", "Address added");
        return "redirect:/account";
    }

    @GetMapping("/edit-address/{id}")
    public String editAddress(@PathVariable("id") Long id, Model model, Principal principal, HttpServletRequest request) {
        if (principal == null) {
            return "redirect:/login";
        }

        HttpSession session = request.getSession();
        String previousPageUrl = request.getHeader("Referer");
        session.setAttribute("previousPageUrl", previousPageUrl);
        AddressDto addressDto = addressService.findById(id);
        model.addAttribute("addressDto", addressDto);
        return "edit-address";
    }

    @PostMapping("/update-address/{id}")
    public String updateAddress(@PathVariable("id") Long id, HttpServletRequest request, Principal principal, @ModelAttribute("addressDto") AddressDto addressDto, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        HttpSession session = request.getSession();
        String previousPageUrl = (String) session.getAttribute("previousPageUrl");

        String referer = request.getHeader("Referer");

        System.out.println(referer);
        Address newAddress = addressService.update(addressDto, id);
        redirectAttributes.addFlashAttribute("message", "Address updated");
        if (previousPageUrl != null) {
            return "redirect:" + previousPageUrl;
        } else {
            return "redirect:/accounts";
        }
    }

    @PostMapping("/add-address-checkout")
    public String AddAddress(@ModelAttribute("addressDto") AddressDto addressDto,
                             Model model, Principal principal, HttpServletRequest request) {
        model.addAttribute("address", addressDto);

        addressService.save(addressDto, principal.getName());
        model.addAttribute("success", "Address Added");

        return "redirect:" + request.getHeader("Referer");
    }


    @RequestMapping(value = "/update-info", method = {RequestMethod.GET, RequestMethod.PUT})
    public String updateCustomer(
            @ModelAttribute("customer") Customer customer,
            Model model,
            RedirectAttributes redirectAttributes,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }
        Customer customerSaved = customerService.saveInfo(customer, new Address());

        redirectAttributes.addFlashAttribute("customer", customerSaved);

        return "redirect:/account";
    }

    @GetMapping("/about")
    public String getAboutUs(Model model) {
        List<Category> categories = categoryService.findAllByActivated();
        model.addAttribute("categories", categories);
        return "about";
    }

    @GetMapping("/contact")
    public String getContactUs(Model model) {
        List<Category> categories = categoryService.findAllByActivated();
        model.addAttribute("categories", categories);
        return "contact";
    }


    /* User profile in progress*/
    @GetMapping("/add-profile")
    public String addProfile(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);

        CustomerDto customerDto = new CustomerDto();
        model.addAttribute("customerDto", customerDto);
        return "profile";
    }

    /* Check referral*/
    @PostMapping("/checkReferralCode")
    public ResponseEntity<String> checkReferralCode(@RequestParam String referralCode) {
        if (referralCode == null || referralCode.trim().isEmpty()) {
            // Input validation: Referral code is null or empty
            return ResponseEntity.badRequest().body("Referral code is required");
        }
        Customer customer = customerService.findByReferralCode(referralCode);
        if (customer != null) {
            return ResponseEntity.ok("valid");

        } else {
            return ResponseEntity.ok("invalid");
        }
    }

}
