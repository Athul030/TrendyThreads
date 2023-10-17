package com.athul.customer.config;

import com.athul.library.exception.CustomLazyInitializationException;
import com.athul.library.exception.TwilioApiException;
import com.athul.library.exception.TwilioVerificationException;
import com.athul.library.model.Customer;
import com.athul.library.repository.CustomerRepository;
import com.athul.library.service.CustomerService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.LazyInitializationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

   private final CustomerRepository customerRepository;

   private final CustomerService customerService;

    public CustomSuccessHandler(CustomerRepository customerRepository, CustomerService customerService) {
        this.customerRepository = customerRepository;
        this.customerService = customerService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {


        String redirectUrl;
        UserDetails userDetails= (UserDetails) authentication.getPrincipal();
        String username= userDetails.getUsername();

        try {
            Customer customer=customerRepository.findByUsername(username);
//            String output = customerService.genrateOTPAndSendOnMobile(customer);
            String output = "success";
            if(output=="success")
//                redirectUrl="/login/otpVerification";
                redirectUrl="/index";
            else
                redirectUrl="/login?otpfailed";

        }catch (TwilioVerificationException e){

            redirectUrl="/login?OTPNumberRegistered";

        }catch (LazyInitializationException e){
            redirectUrl="/login?OTPNumberNotRegistered";

        } catch (TwilioApiException e) {
            redirectUrl="/login?OTPNumberNotRegistered";
        }
        new DefaultRedirectStrategy().sendRedirect(request,response,redirectUrl);


    }
}
