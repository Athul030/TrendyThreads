package com.athul.admin.controller;



import com.athul.library.dto.CustomerDto;
import com.athul.library.dto.ProductDto;
import com.athul.library.model.Customer;
import com.athul.library.service.CustomerService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class CustomerController {

    private CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/users")
    public String products(Model model , Principal principal){
        if(principal==null){
            return "redirect:/login";
        }
        List<Customer> customerList= customerService.findAll();
        model.addAttribute("title", "Manage Users");
        model.addAttribute("users", customerList);
        model.addAttribute("size",customerList.size());
        return "users";
    }


    @RequestMapping(value="/unblock-users/{id}", method={ RequestMethod.PUT,RequestMethod.GET})
    public String unblockUser(@PathVariable("id") Long id, RedirectAttributes attributes){
        try {
            customerService.unblockById(id);
            attributes.addFlashAttribute("success", "Unblocked successfully");
        }catch (Exception e){
            e.printStackTrace();
            attributes.addFlashAttribute("error","Failed to Unblock");
        }
        return "redirect:/users";
    }


    @RequestMapping(value="/block-users/{id}", method={RequestMethod.GET, RequestMethod.PUT})
    public String blockUser(@PathVariable("id") Long id, RedirectAttributes attributes){

        try {
            customerService.blockById(id);
            attributes.addFlashAttribute("success", "Blocked successfully");
        }catch (Exception e){
            e.printStackTrace();
            attributes.addFlashAttribute("error","Failed to delete");
        }
        return "redirect:/users";
    }


    @GetMapping("/update-users/{id}")
    public String products(@PathVariable("id") Long id, Principal principal){
        if(principal==null){
            return "redirect:/login";
        }

        return "underConstruction";
    }

    @GetMapping("/search-users/0")
    public String searchProduct(@RequestParam(value = "keyword") String keyword,
                                Model model) {
        List<CustomerDto> customerDto = customerService.searchCustomer(keyword);

        model.addAttribute("title", "Result Search Customers");
        model.addAttribute("size", customerDto.size());
        model.addAttribute("customers", customerDto);

        return "result-user";

    }
}
