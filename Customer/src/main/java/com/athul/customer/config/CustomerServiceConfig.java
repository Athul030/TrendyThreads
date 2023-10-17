package com.athul.customer.config;

import com.athul.library.model.Customer;
import com.athul.library.repository.CustomerRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;


@Component
public class CustomerServiceConfig implements UserDetailsService {


    private CustomerRepository customerRepository;

    public CustomerServiceConfig(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer=customerRepository.findByUsername(username);
        if(customer==null){
            throw new UsernameNotFoundException("Could not find username");
        }
        if(customer.is_blocked()){
            throw new LockedException("User is blocked");
        }
        return new User(customer.getUsername(),
                customer.getPassword(),
                customer.getRoles().
                        stream().
                        map(role->new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList()));
    }
}
