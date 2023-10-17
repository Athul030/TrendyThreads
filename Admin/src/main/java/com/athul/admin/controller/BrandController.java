package com.athul.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BrandController {

    @GetMapping("/brands")
    public String brands(Model model){
        model.addAttribute("title","Brand");
        return "brands";
    }
}
