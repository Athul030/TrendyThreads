package com.athul.admin.controller;

import com.athul.library.dto.AdminDto;
import com.athul.library.model.Admin;
import com.athul.library.repository.AdminRepository;
import com.athul.library.service.AdminService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {


    private AdminService adminService;

    private BCryptPasswordEncoder passwordEncoder;

    public LoginController(AdminService adminService, BCryptPasswordEncoder passwordEncoder) {
        this.adminService = adminService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginForm( Model model){

        model.addAttribute("title","Login");
        return "login";
    }




    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("title","Register");
        model.addAttribute("adminDto",new AdminDto());
        return "register";

    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model){
        model.addAttribute("title","Forgot Password");
        return "forgot-password";
    }

    @PostMapping("/register-new")
    public String addNewAdmin(@Valid  @ModelAttribute ("adminDto") AdminDto adminDto,
                              BindingResult result,
                              Model model){
        try{
            if(result.hasErrors()){
                model.addAttribute("adminDto",adminDto);
                result.toString();
                return "register"; // Return to the registration form with validation errors displayed.
            }
            String username=adminDto.getUsername();
            Admin admin = adminService.findByUsername(username);

            if(admin!=null){
                model.addAttribute("adminDto",adminDto);
                model.addAttribute("emailError", "Your email is already used!");
                return "register"; // Return to the registration form with a message if email is already registered.
            }
            if(adminDto.getPassword().equals(adminDto.getRepeatPassword())) {
                adminDto.setPassword(passwordEncoder.encode(adminDto.getPassword()));
                // If the passwords match, save the admin user's data.
                adminService.save(adminDto);
                model.addAttribute("success", "Registered successfully!");
                model.addAttribute("adminDto", adminDto); //maybe unnecessary, no need of prepoulating data in the event of successful register
            }else{
                model.addAttribute("adminDto",adminDto);
                model.addAttribute("passwordError", "Your password maybe wrong! Check again!");
                return "register"; // Return to the registration form with a message if passwords don't match.
            }

        }catch (Exception e){
            e.printStackTrace();
            model.addAttribute("errors","Server error, please try again later");
        }

        return "register"; // Return to the registration form, possibly with a success or error message.
    }
}
