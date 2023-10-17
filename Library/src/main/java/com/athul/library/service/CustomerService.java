package com.athul.library.service;

import com.athul.library.dto.CustomerDto;
import com.athul.library.dto.ProductDto;
import com.athul.library.model.Address;
import com.athul.library.model.Category;
import com.athul.library.model.Customer;

import java.util.List;


public interface CustomerService {

    CustomerDto save(CustomerDto customerDto);

    String shareReferralCode(String referralCode, String emailAddress);

//    void saveOtp(CustomerDto customerDto);

    Customer findByUsername(String username);

    Customer findByReferralCode(String referralCode);

    List<Customer> findAll();

    Customer update(Customer customer);


    void blockById(Long id);

    void unblockById(Long id);

    List<CustomerDto> searchCustomer(String keyword);

    String genrateOTPAndSendOnMobile(Customer customer);

    String genrateOTPAndSendOnEmail(String username);

    boolean verifyOTP(long otp, String username);

    Customer saveInfo(Customer customer, Address address);

    Customer findById(long id);


}
