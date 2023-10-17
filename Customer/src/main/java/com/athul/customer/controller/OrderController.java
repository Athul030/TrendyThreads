package com.athul.customer.controller;

import com.athul.library.dto.AddressDto;
import com.athul.library.dto.CouponDto;
import com.athul.library.exception.OutOfStockException;
import com.athul.library.model.*;
import com.athul.library.repository.OrderDetailRepository;
import com.athul.library.repository.WalletHistoryRepository;
import com.athul.library.service.*;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.logging.Logger;


import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    private final CustomerService customerService;
    private final OrderService orderService;

    private final OrderDetailRepository orderDetailRepository;

    private final ShoppingCartService shoppingCartService;

    private final AddressService addressService;

    private final CouponService couponService;

    private final WalletService walletService;

    private final CategoryService categoryService;


    public OrderController(CustomerService customerService, OrderService orderService, OrderDetailRepository orderDetailRepository, ShoppingCartService shoppingCartService, AddressService addressService, CouponService couponService, WalletService walletService, CategoryService categoryService) {
        this.customerService = customerService;
        this.orderService = orderService;
        this.orderDetailRepository = orderDetailRepository;
        this.shoppingCartService = shoppingCartService;
        this.addressService = addressService;
        this.couponService = couponService;
        this.walletService = walletService;
        this.categoryService = categoryService;
    }

    private static final Logger logger = Logger.getLogger(OrderController.class.getName());



    @GetMapping("/order")
    public String order(Principal principal, Model model){
        if(principal == null){
            return "redirect:/login";
        }
        String username= principal.getName();
        Customer customer=customerService.findByUsername(username);
        List<Order> orderList=customer.getOrders();

        model.addAttribute("orders",orderList);

        return "order";
    }

    @GetMapping("/shop-checkout")
    public String checkout(Model model, Principal principal, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest){
        if(principal == null){
            return "redirect:/login";
        }

        if (redirectAttributes.getFlashAttributes().containsKey("errorMessage")) {
            String errorMessage = (String) redirectAttributes.getFlashAttributes().get("errorMessage");
            model.addAttribute("errorMessage", errorMessage);
        }


        AddressDto addressDto=new AddressDto();
        model.addAttribute("addressDto", addressDto);

        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);
        if(customer.getAddress().isEmpty()){
            model.addAttribute("customer", customer);
            model.addAttribute("error", "You must fill the information before checkout!");
            return "add-address";
        }else{
            Address address=new Address();
            model.addAttribute("addressEnter", address);
            model.addAttribute("addresses",customer.getAddress());
            model.addAttribute("customer", customer);

            Wallet wallet=walletService.findByCustomer(customer);
            model.addAttribute("wallet",wallet);
            List<CouponDto> couponDto=couponService.getAllCoupons();
            model.addAttribute("coupons",couponDto);
            ShoppingCart cart = customer.getCart();

            model.addAttribute("cart", cart);
        }

        /*For the name*/
        HttpSession httpSession1= httpServletRequest.getSession();
        String name=null;
        if(httpSession1!=null) {
            name = httpServletRequest.getRemoteUser();
        }
        model.addAttribute("name",name);
        return "shop-checkout2";
    }

//    @RequestMapping(value = "/cancel-order/{id}", method = {RequestMethod.PUT, RequestMethod.GET})
//    public String cancelOrder(@PathVariable("id") Long id, RedirectAttributes attributes) {
//        orderService.cancelOrder(id);
//        attributes.addFlashAttribute("success", "Cancel order successfully!");
//        return "redirect:/order";
//    }




    /* extra methods */

    @RequestMapping(value = "/check-out/apply-coupon", method = RequestMethod.POST, params = "action=apply")
    public String applyCoupon(@RequestParam("couponCode")String couponCode,Principal principal,
                              RedirectAttributes attributes,HttpSession session){


        if(couponService.findByCouponCode(couponCode.toUpperCase())) {

            Coupon coupon = couponService.findByCode(couponCode.toUpperCase());
            ShoppingCart cart = customerService.findByUsername(principal.getName()).getCart();
            Double totalPrice = cart.getTotalPrice();

            if(coupon.getMinOrderAmount()>totalPrice){

                String message = "Minimum order amount to apply the coupon " + coupon.getCode() + " is " + coupon.getMinOrderAmount();
                attributes.addFlashAttribute("minOrderAmount", message);
                return "redirect:/shop-checkout";
            }
            session.setAttribute("totalPrice",totalPrice);
            Double newTotalPrice = couponService.applyCoupon(couponCode.toUpperCase(), totalPrice);

            shoppingCartService.updateTotalPrice(newTotalPrice, principal.getName());

            attributes.addFlashAttribute("success", "Coupon applied Successfully");
            attributes.addAttribute("couponCode", couponCode);
            attributes.addAttribute("couponOff", coupon.getOffPercentage());
        }else{
            attributes.addFlashAttribute("error", "Coupon Code invalid");
        }
        return "redirect:/shop-checkout";

    }

    @RequestMapping(value = "/check-out/apply-coupon", method = RequestMethod.POST, params = "action=remove")
    public String removeCoupon(Principal principal,RedirectAttributes attributes,
                               HttpSession session){

        Double totalPrice = (Double) session.getAttribute("totalPrice");
        shoppingCartService.updateTotalPrice(totalPrice, principal.getName());
        attributes.addFlashAttribute("success", "Coupon removed Successfully");

        return "redirect:/shop-checkout";
    }



    @RequestMapping(value = "/add-order",method = RequestMethod.POST)
    @ResponseBody
    public String createOrder(@RequestBody Map<String,Object> data, Principal principal, HttpSession session, Model model) throws RazorpayException, RazorpayException {
        String paymentMethod = data.get("payment_Method").toString();
        Long address_id=Long.parseLong(data.get("addressId").toString());
        Double oldTotalPrice= (Double)session.getAttribute("totalPrice");
        Customer customer = customerService.findByUsername(principal.getName());
        ShoppingCart cart = customer.getCart();
        if(paymentMethod.equals("COD")){
            Order order = orderService.save(cart,address_id,paymentMethod,oldTotalPrice);
            session.removeAttribute("totalItems");
            session.removeAttribute("totalPrice");
            session.setAttribute("orderId",order.getId());
            model.addAttribute("order", order);
            model.addAttribute("page", "Order Detail");
            model.addAttribute("success", "Order Added Successfully");
            JSONObject options = new JSONObject();
            options.put("status","Cash");

            return options.toString();
        }else if(paymentMethod.equals("Wallet")){
            WalletHistory walletHistory=walletService.debit(customer.getWallet(),cart.getTotalPrice());
            Order order = orderService.save(cart,address_id,paymentMethod,oldTotalPrice);
            walletService.saveOrderId(order,walletHistory);

            session.removeAttribute("totalItems");
            session.removeAttribute("totalPrice");
            session.setAttribute("orderId",order.getId());
            model.addAttribute("order", order);
            model.addAttribute("page", "Order Detail");
            model.addAttribute("success", "Order Added Successfully");
            JSONObject options = new JSONObject();
            options.put("status","Wallet");
            return options.toString();
        }else{
            Order order = orderService.save(cart,address_id,paymentMethod,oldTotalPrice);
            String orderId=order.getId().toString();
            session.removeAttribute("totalItems");
            session.removeAttribute("totalPrice");
            session.setAttribute("orderId",order.getId());
            model.addAttribute("order", order);
            model.addAttribute("page", "Order Detail");
            model.addAttribute("success", "Order Added Successfully");
            RazorpayClient razorpayClient=new RazorpayClient("rzp_test_UfR9pHHnga86KW","VtfZbqoqDJAiRtGJQ2ZMoLaW");
            JSONObject options = new JSONObject();
            options.put("amount",order.getTotalPrice()*100);
            options.put("currency","INR");
            options.put("receipt",orderId);
            com.razorpay.Order orderRazorPay = razorpayClient.orders.create(options);
            return orderRazorPay.toString();
        }

    }

    @ExceptionHandler(OutOfStockException.class)
    public String handleOutOfStockException(OutOfStockException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        logger.info("Error message: " + ex.getMessage());
        return "redirect:/shop-checkout";
    }

    @RequestMapping(value = "/verify-payment",method = RequestMethod.POST)
    @ResponseBody
    public String verifyPayment(@RequestBody Map<String,Object> data,HttpSession session,Principal principal) throws RazorpayException {

        String secret= "VtfZbqoqDJAiRtGJQ2ZMoLaW";
        String order_id= data.get("razorpay_order_id").toString();
        String payment_id=data.get("razorpay_payment_id").toString();
        String signature=data.get("razorpay_signature").toString();
        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", order_id);
        options.put("razorpay_payment_id", payment_id);
        options.put("razorpay_signature", signature);

        boolean status =  Utils.verifyPaymentSignature(options, secret);
        Order order=orderService.findOrderById((Long)session.getAttribute("orderId"));
        if(status){
            orderService.updatePayment(order,status);
            Customer customer=customerService.findByUsername(principal.getName());
            ShoppingCart cart = customer.getCart();
            shoppingCartService.deleteCartById(cart.getId());
        }else {
            orderService.updatePayment(order, status);
        }
        JSONObject response = new JSONObject();
        response.put("status",status);

        return response.toString();
    }


    @GetMapping("/order-confirmation")
    public String getOrderConfirmation(Model model,HttpSession session){



        if(session.getAttribute("orderId")==null){
            return "redirect:/index";
        }
        Long order_id=(Long)session.getAttribute("orderId");

        Order order=orderService.findOrderById(order_id);
        String paymentMethod = order.getPaymentMethod();
        if (paymentMethod.equals("COD")){
            model.addAttribute("payment","Pending");
        }else{
            model.addAttribute("payment","Successful");
        }
        model.addAttribute("orders", order);
        model.addAttribute("success", "Order Added Successfully");
        session.removeAttribute("orderId");

        List<Category> categories=categoryService.findAllByActivated();
        model.addAttribute("categories",categories);

        return "order";
    }




    @GetMapping("/cancel-order/{id}")
    public String cancelOrder(@PathVariable("id")long order_id, RedirectAttributes attributes){
        orderService.cancelOrder(order_id);
        attributes.addFlashAttribute("success", "Cancel order successfully!");
        return "redirect:/account?tab=orders";
        /* change this to my tab*/
    }

    @GetMapping("/return-order/{id}")
    public String returnOrder(@PathVariable("id")long order_id, RedirectAttributes attributes,
                              Principal principal){
        Customer customer=customerService.findByUsername(principal.getName());
        orderService.returnOrder(order_id,customer);
        attributes.addFlashAttribute("success", "Order Returned successfully!");
        return "redirect:/account?tab=orders";
    }


    /*not using now*/
    @GetMapping("/order-view/{id}")
    public String orderView(@PathVariable("id")long order_id,Model model,HttpSession session){
        Order order=orderService.findOrderById(order_id);
        String paymentMethod = order.getPaymentMethod();
        if (paymentMethod.equals("COD")){
            model.addAttribute("payment","Pending");
        }else {
            model.addAttribute("payment", "Paid");
        }
        Customer customer=customerService.findById(order.getCustomer().getId());
        Address address = addressService.findDefaultAddress(customer.getId());
        model.addAttribute("order",order);
        model.addAttribute("address",address);

        return "order-view";
    }


    @GetMapping("/order-tracking/{id}")
    public String orderTrack(@PathVariable("id")long order_id,Model model,HttpSession session){

        Order order=orderService.findOrderById(order_id);
        String paymentMethod = order.getPaymentMethod();
        if (paymentMethod.equals("COD")){
            model.addAttribute("payment","Pending");
        }else {
            model.addAttribute("payment", "Paid");
        }
        Date currentTime = new Date();
        Customer customer=customerService.findById(order.getCustomer().getId());
        Address address = addressService.findDefaultAddress(customer.getId());
        if(order.getOrderStatus().equals("Pending")){
            int pending=1;
            model.addAttribute("pending",pending);
        }else if(order.getOrderStatus().equals("Confirmed")){
            int pending=1;
            int confirmed=2;
            model.addAttribute("pending",pending);
            model.addAttribute("confirmed",confirmed);
        }else if(order.getOrderStatus().equals("Shipped")){
            int pending=1;
            int confirmed=2;
            int shipped=3;
            model.addAttribute("pending",pending);
            model.addAttribute("confirmed",confirmed);
            model.addAttribute("shipped",shipped);
        }else if(order.getOrderStatus().equals("Delivered")){
            int pending=1;
            int confirmed=2;
            int shipped=3;
            int delivered=4;
            model.addAttribute("pending",pending);
            model.addAttribute("confirmed",confirmed);
            model.addAttribute("shipped",shipped);
            model.addAttribute("delivered",delivered);
        }
        model.addAttribute("order",order);
        model.addAttribute("time",currentTime);
        model.addAttribute("address",address);

        List<Category> categories=categoryService.findAllByActivated();
        model.addAttribute("categories",categories);

        return "order-track";
    }



}
