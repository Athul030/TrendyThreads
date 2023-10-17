package com.athul.admin.controller;

import com.athul.library.model.Address;
import com.athul.library.model.Customer;
import com.athul.library.model.Order;
import com.athul.library.repository.OrderDetailRepository;
import com.athul.library.repository.OrderRepository;
import com.athul.library.service.AddressService;
import com.athul.library.service.CustomerService;
import com.athul.library.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class OrderController {

    private final OrderService orderService;

    private final CustomerService customerService;

    private final OrderRepository orderRepository;

    private final OrderDetailRepository orderDetailRepository;

    private final AddressService addressService;

    public OrderController(OrderService orderService, CustomerService customerService, OrderRepository orderRepository, OrderDetailRepository orderDetailRepository, AddressService addressService) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.addressService = addressService;
    }

    @GetMapping("/orders")
    public String Orders(Model model, Principal principal,
                         @RequestParam(name="status", required = false,defaultValue = "")String orderStatus,
                         @RequestParam(name="orderId", required=false, defaultValue="0")long order_id){

        if(principal==null){
            return "redirect:/login";
        }else{

        }
        orderService.updateOrderStatus(orderStatus,order_id);
        List<Order> orders= orderService.findALlOrders();
        model.addAttribute("size",orders.size());
        model.addAttribute("orders",orders);
        return "orders";
    }

    @GetMapping("/accept-order/{id}")
    public String acceptOrder(@PathVariable("id")long order_id, RedirectAttributes attributes){

        orderService.acceptOrder(order_id);
        attributes.addFlashAttribute("success","Order Accepted");
        return "redirect:/orders";
    }

    @GetMapping("/cancel-order/{id}")
    public String cancelOrder(@PathVariable ("id") long order_id, RedirectAttributes attributes){
        orderService.cancelOrder(order_id);
        attributes.addFlashAttribute("cancelled", "Order Cancelled");
        return "redirect:/orders";
    }


    @GetMapping("/order-view/{id}")
    public String orderView(@PathVariable("id")long order_id,Model model){
        Order order=orderService.findOrderById(order_id);
        Customer customer=customerService.findById(order.getCustomer().getId());
        Address address = addressService.findDefaultAddress(customer.getId());
        model.addAttribute("order",order);
        model.addAttribute("address",address);

        return "order-view";
    }

    @GetMapping("/get-order-status/{orderId}")
    public ResponseEntity<String> getOrderStatus(@PathVariable Long orderId) {
        String orderStatus = orderService.getOrderStatus(orderId);

        if (orderStatus != null) {
            return ResponseEntity.ok(orderStatus);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
