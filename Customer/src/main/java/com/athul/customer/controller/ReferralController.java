package com.athul.customer.controller;

import com.athul.library.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReferralController {

    private final CustomerService customerService;

    public ReferralController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/shareReferral")
    public ResponseEntity<String> shareReferral(@RequestParam String referralCode,
                                                @RequestParam String emailAddress
                                ){
        System.out.println("start");
        String success="success";
        String result=customerService.shareReferralCode(referralCode,emailAddress);
        if (result.equals(success)) {
            return ResponseEntity.ok("success");

        } else {
            return ResponseEntity.ok("invalid");
        }
    }
}
