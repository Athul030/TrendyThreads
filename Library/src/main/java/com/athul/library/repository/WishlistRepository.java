package com.athul.library.repository;

import com.athul.library.model.Customer;
import com.athul.library.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist,Long> {

    List<Wishlist> findAllByCustomerId(long id);

    @Query(value = "SELECT EXISTS (SELECT FROM wishlist WHERE customer_id = :customerId AND product_id=:productId)",nativeQuery = true)
    boolean findByProductIdAndCustomerId(@Param("productId") long productId, @Param("customerId") long customerId);

    Wishlist findById(long id);

    Wishlist findByCustomerAndWishlistName(Customer customer, String wishlistName);
}
