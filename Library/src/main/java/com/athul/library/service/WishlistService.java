package com.athul.library.service;

import com.athul.library.model.Customer;
import com.athul.library.model.Wishlist;

import java.util.List;

public interface WishlistService {


    List<Wishlist> findAllByCustomer(Customer customer);

    boolean findByProductId(long id,Customer customer);

    Wishlist save(long productId,Customer customer);

    void deleteWishlist(long id);

    Wishlist findById(long id);

    void createWishlist(String wishlistName, Customer customer);
}
