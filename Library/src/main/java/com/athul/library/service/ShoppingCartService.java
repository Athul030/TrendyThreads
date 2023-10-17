package com.athul.library.service;

import com.athul.library.dto.ProductDto;
import com.athul.library.dto.ShoppingCartDto;
import com.athul.library.model.Customer;
import com.athul.library.model.Product;
import com.athul.library.model.ShoppingCart;

public interface ShoppingCartService {

    ShoppingCart addItemToCart(ProductDto productDto, int quantity, String username);

    ShoppingCart updateCart(ProductDto productDto, int quantity, String username);

    ShoppingCart removeItemFromCart(ProductDto productDto, String username);

    ShoppingCartDto addItemToCartSession(ShoppingCartDto cartDto, ProductDto productDto, int quantity);

    ShoppingCartDto updateCartSession(ShoppingCartDto cartDto, ProductDto productDto, int quantity);

    ShoppingCartDto removeItemFromCartSession(ShoppingCartDto cartDto, ProductDto productDto, int quantity);

    ShoppingCart combineCart(ShoppingCartDto cartDto, ShoppingCart cart);


    void deleteCartById(Long id);

    ShoppingCart getCart(String username);

    ShoppingCart updateTotalPrice(Double newTotalPrice,String username);

}
