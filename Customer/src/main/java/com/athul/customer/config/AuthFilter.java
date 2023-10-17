package com.athul.customer.config;

import com.athul.library.model.Customer;
import com.athul.library.repository.CustomerRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class AuthFilter implements Filter {

    private static final List<String> EXCLUDED_ENDPOINTS = Arrays.asList("/user/register/**", "/user/login/**",  "/user/index/**","/user/**");
    private final AntPathMatcher pathMatcher = new AntPathMatcher();


    private final CustomerRepository customerRepository;

    public AuthFilter(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }



    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String requestURI = httpRequest.getRequestURI();
        boolean isExcludedEndpoint = EXCLUDED_ENDPOINTS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
        if (isExcludedEndpoint) {
            filterChain.doFilter(servletRequest,servletResponse);
        } else {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (securityContext.getAuthentication() != null) {
                UserDetails user = (UserDetails) securityContext.getAuthentication().getPrincipal();
                Customer customer = customerRepository.findByUsername(user.getUsername());
                if (customer.isActive()) {
                    filterChain.doFilter(servletRequest, servletResponse);
                } else {
                    HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
                    httpResponse.sendRedirect("/login");
                }
            }else {
                HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
                httpResponse.sendRedirect("/login");
            }
        }
    }


}
