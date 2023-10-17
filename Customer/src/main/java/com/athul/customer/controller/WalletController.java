package com.athul.customer.controller;

import com.athul.library.dto.WalletHistoryDto;
import com.athul.library.model.Customer;
import com.athul.library.model.Wallet;
import com.athul.library.model.WalletHistory;
import com.athul.library.repository.WalletHistoryRepository;
import com.athul.library.service.CustomerService;
import com.athul.library.service.WalletService;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
public class WalletController {

    private WalletService walletService;

    private WalletHistoryRepository walletHistoryRepository;

    private CustomerService customerService;

    public WalletController(WalletService walletService, WalletHistoryRepository walletHistoryRepository, CustomerService customerService) {
        this.walletService = walletService;
        this.walletHistoryRepository = walletHistoryRepository;
        this.customerService = customerService;
    }

    @GetMapping("/wallets")
    public String getWalletTab(Principal principal, Model model){
        if(principal==null){
            return "redirect:/login";
        }
        Customer customer=customerService.findByUsername(principal.getName());
        Wallet wallet=walletService.findByCustomer(customer);
        List<WalletHistoryDto> walletHistoryDtoList=walletService.findAllById(wallet.getId());
        model.addAttribute("walletHistoryList", walletHistoryDtoList );
        model.addAttribute("wallet",wallet);
        return "wallet";
    }



    @RequestMapping(value = "/add-wallet",method = RequestMethod.POST)
    @ResponseBody
    public String  addWallet(@RequestBody Map<String,Object> data, Principal principal,
                            HttpSession session, Model model) throws RazorpayException {

        if(principal==null){
            return "redirect:/login";
        }


        Customer customer=customerService.findByUsername(principal.getName());

        double amount = Double.parseDouble(data.get("amount").toString());
        WalletHistory walletHistory=walletService.save(amount,customer);

        String walletHistoryId = walletHistory.getId().toString();

        session.setAttribute("walletHistoryId",walletHistory.getId());

        model.addAttribute("success","Money Added Successfully");
        RazorpayClient razorpayClient=new RazorpayClient("rzp_test_UfR9pHHnga86KW","VtfZbqoqDJAiRtGJQ2ZMoLaW");

        JSONObject options = new JSONObject();
        options.put("amount",amount*100);
        options.put("currency","INR");
        options.put("receipt",walletHistoryId);
        com.razorpay.Order orderRazorPay = razorpayClient.orders.create(options);
        return orderRazorPay.toString();

    }

    @RequestMapping(value = "/verify-wallet",method = RequestMethod.POST)
    @ResponseBody
    public String verifyWalletPayment(@RequestBody Map<String,Object> data,HttpSession session,Principal principal) throws RazorpayException {

        System.out.println("verify-wallet");
        String secret= "VtfZbqoqDJAiRtGJQ2ZMoLaW";
        String order_id= data.get("razorpay_order_id").toString();
        String payment_id=data.get("razorpay_payment_id").toString();
        String signature=data.get("razorpay_signature").toString();
        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", order_id);
        options.put("razorpay_payment_id", payment_id);
        options.put("razorpay_signature", signature);

        boolean status =  Utils.verifyPaymentSignature(options, secret);
        WalletHistory walletHistory=walletService.findById((Long)session.getAttribute("walletHistoryId"));
        Customer customer=customerService.findByUsername(principal.getName());
        if(status){
            walletService.updateWallet(walletHistory,customer,status);
        }else {
            walletService.updateWallet(walletHistory,customer,status);
        }
        JSONObject response = new JSONObject();
        response.put("status",status);

        session.removeAttribute("walletHistoryId");

        return response.toString();
    }
}
