package com.athul.library.service;

import com.athul.library.model.Address;
import com.athul.library.model.Customer;
import com.athul.library.model.Order;
import com.athul.library.model.ShoppingCart;

import java.util.List;

public interface OrderService {

//    Order save(ShoppingCart shoppingCart, Address shippingAddress);

    String getOrderStatus(Long orderId);

    Order save(ShoppingCart cart,long address_id,String paymentMethod,Double oldTotalPrice);

    List<Order> findAll(String username);

    List<Order> findALlOrders();

    void acceptOrder(Long id);

    void cancelOrder(Long id);

    Order findOrderById(long id);

    void updatePayment(Order order,boolean status);

    void returnOrder(long id, Customer customer);

    void updateOrderStatus(String status, long order_id);

}
