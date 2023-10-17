package com.athul.customer.controller;

import com.athul.customer.config.CustomerServiceConfig;
import com.athul.library.dto.CustomerDto;
import com.athul.library.dto.ProductDto;
import com.athul.library.model.Category;
import com.athul.library.model.Customer;
import com.athul.library.repository.CustomerRepository;
import com.athul.library.service.CategoryService;
import com.athul.library.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AuthController {


    private final CustomerService customerService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;
    private final CustomerServiceConfig customerServiceConfig;

    private final CategoryService categoryService;


    public AuthController(CustomerService customerService, BCryptPasswordEncoder passwordEncoder, CustomerRepository customerRepository, CustomerServiceConfig customerServiceConfig, CategoryService categoryService) {
        this.customerService = customerService;
        this.passwordEncoder = passwordEncoder;
        this.customerRepository = customerRepository;
        this.customerServiceConfig = customerServiceConfig;
        this.categoryService = categoryService;
    }

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("title", "Login");

        List<Category> categories=categoryService.findAllByActivated();
        model.addAttribute("categories",categories);

        return "login";
    }



    @ModelAttribute("user")
    public CustomerDto customerDto() {

        return new CustomerDto();
    }

    @PostMapping("/login")
    public void loginUser(@ModelAttribute("user")
                          CustomerDto customerDto) {
        customerServiceConfig.loadUserByUsername(customerDto.getUsername());
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Model model) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.setInvalidateHttpSession(true);
        logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        model.addAttribute("logout","You have been logged out");
        return "/login";
    }

    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("title", "Register");
        model.addAttribute("customerDto",new CustomerDto());
        List<Category> categories=categoryService.findAllByActivated();
        model.addAttribute("categories",categories);
        return "register";
    }

    @PostMapping("/registerNew")
    public String registerCustomer(@Valid @ModelAttribute("customerDto") CustomerDto customerDto,
                                   @RequestParam(name = "referralCode", required = false) String referralCode,
                                   BindingResult result,
                                   Model model) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("customerDto", customerDto);
                return "register";
            }
            String username = customerDto.getUsername();
            Customer customer = customerService.findByUsername(username);
            if (customer != null) {
                model.addAttribute("customerDto", customerDto);
                model.addAttribute("error", "This email is already registered");
                return "register";
            }
            if (customerDto.getPassword().equals(customerDto.getRepeatPassword())) {
                customerDto.setPassword(passwordEncoder.encode(customerDto.getPassword()));
                System.out.println("authcontrollersavestarted");
                customerService.save(customerDto);
                model.addAttribute("success", "Register successfully!");
            } else {
                model.addAttribute("customerDto", customerDto);
                model.addAttribute("passwordError", "Password is not same");
                return "register";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Server is error, try again later!");
        }
        return "register";
    }


    @GetMapping("/forgot-password")
    public String forgotPasswordOTP(Model model, CustomerDto customerDto){

        model.addAttribute("title", "Forgot Password- OTP");
        model.addAttribute("username",customerDto);

        List<Category> categories=categoryService.findAllByActivated();
        model.addAttribute("categories",categories);
        return "enterUsername";
    }

    @PostMapping("/forgot-password")
    public String forgotPasswordOTPSend(@ModelAttribute("username") CustomerDto customerDto, Model model){
        String otp= customerService.genrateOTPAndSendOnEmail(customerDto.getUsername());
        model.addAttribute("data",customerDto);
        model.addAttribute("otpGenerationTime",System.currentTimeMillis());
        List<Category> categories=categoryService.findAllByActivated();
        model.addAttribute("categories",categories);
        return "otpScreenEmail";
    }



    @PostMapping("/forgot-password/otpVerification")
    public String otpSentEmailPost(@ModelAttribute("data") CustomerDto customerDto , Model model, RedirectAttributes attributes) {
        boolean isOTPValid = customerService.verifyOTP(customerDto.getOtp(),customerDto.getUsername());
        if (isOTPValid) {
            model.addAttribute("customerDto",customerDto);
            return "passwordResetPage";
        } else {
            model.addAttribute("error", "Invalid OTP. Please try again.");
            return "otpScreenEmail";
        }
    }


    @PostMapping("/passwordResetPage")
    public String passwordResetPage(@ModelAttribute("customerDto") CustomerDto customerDto, Model model, RedirectAttributes redirectAttributes){

        System.out.println(customerDto);
        System.out.println("here");
        System.out.println(customerDto.getPassword());
        System.out.println(customerDto.getRepeatPassword());
        if(customerDto.getPassword().equals(customerDto.getRepeatPassword())){
            Customer customer=customerRepository.findByUsername(customerDto.getUsername());
            customer.setPassword(passwordEncoder.encode(customerDto.getPassword()));
            customerRepository.save(customer);
            redirectAttributes.addFlashAttribute("success", "Password is changed");
        }
        else{
            redirectAttributes.addFlashAttribute("error", "Passwords are not same");

        }

        return "redirect:/login";
    }


    @GetMapping("/login/otpVerification")
    public String otpSent(Model model,CustomerDto customerDto, HttpServletRequest httpServletRequest) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        UserDetails user = (UserDetails) securityContext.getAuthentication().getPrincipal();
        Customer customer = customerRepository.findByUsername(user.getUsername());

        if (customer != null) {
            customer.setActive(false);
            customerRepository.save(customer);
        }
        model.addAttribute("otpValue", customerDto);
        List<Category> categories=categoryService.findAllByActivated();
        model.addAttribute("categories",categories);
        return "otpScreen";

    }

    @PostMapping("/login/otpVerification")
    public String otpVerification(@ModelAttribute("otpValue") CustomerDto customerDto, HttpServletRequest httpServletRequest) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        UserDetails user = (UserDetails) securityContext.getAuthentication().getPrincipal();
        Customer customer = customerRepository.findByUsername(user.getUsername());

        if (httpServletRequest.getRequestURI().equals("/user/index")) {

            if (customer.getOtp() == customerDto.getOtp()) {
                customer.setActive(true);
                customerRepository.save(customer);
                return "redirect:/index";
            } else {
                return "redirect:/login/otpVerification?error";
            }
        } else {
            return "redirect:/index";
        }
    }


}
