package com.athul.library.service;

import com.athul.library.exception.DuplicateWishlistNameException;
import com.athul.library.model.Customer;
import com.athul.library.model.Product;
import com.athul.library.model.Wishlist;
import com.athul.library.repository.ProductRepository;
import com.athul.library.repository.WishlistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistServiceImpl implements WishlistService{

    private WishlistRepository wishlistRepository;

    private ProductService productService;

    public WishlistServiceImpl(WishlistRepository wishlistRepository, ProductService productService) {
        this.wishlistRepository = wishlistRepository;
        this.productService = productService;
    }

    @Override
    public List<Wishlist> findAllByCustomer(Customer customer) {
        List<Wishlist>Wishlists=wishlistRepository.findAllByCustomerId(customer.getId());
        return Wishlists;
    }

    @Override
    public boolean findByProductId(long productId, Customer customer) {
        boolean exists= wishlistRepository.findByProductIdAndCustomerId(productId,customer.getId());
        return exists;
    }

    @Override
    public Wishlist save(long productId, Customer customer) {
        Product product=productService.findBYId(productId);
        Wishlist wishlist=new Wishlist();
        wishlist.setProduct(product);
        wishlist.setCustomer(customer);
        wishlistRepository.save(wishlist);
        return wishlist;
    }

    @Override
    public void deleteWishlist(long id) {
        Wishlist wishlist=wishlistRepository.findById(id);
        wishlistRepository.delete(wishlist);
    }

    @Override
    public Wishlist findById(long id) {
        return wishlistRepository.findById(id);
    }

    @Override
    public void createWishlist(String wishlistName, Customer customer) {

        Wishlist existingWishlist = wishlistRepository.findByCustomerAndWishlistName(customer, wishlistName);

        if (existingWishlist != null) {
            throw new DuplicateWishlistNameException("A wishlist with the same name already exists.");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setWishlistName(wishlistName);
        wishlist.setCustomer(customer);
        wishlistRepository.save(wishlist);
    }
}
