package com.athul.library.repository;

import com.athul.library.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByUsername(String username);

    Customer findByReferralCode(String referralCode);


    @Query("select u from Customer u where LOWER(u.firstName) like %?1% or LOWER(u.lastName) like %?1% or LOWER(u.username) like %?1%")
    List<Customer> findAllByNameOrEmail(String keyword);

    Customer findById(long id);

}
