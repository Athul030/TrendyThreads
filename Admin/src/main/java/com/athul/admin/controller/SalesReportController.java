package com.athul.admin.controller;

import com.athul.library.dto.ProductDto;
import com.athul.library.model.Order;
import com.athul.library.model.Product;
import com.athul.library.service.OrderService;
import com.athul.library.service.ProductService;
import com.athul.library.service.SalesReportService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
public class SalesReportController {

    private OrderService orderService;
    private ProductService productService;
    private SalesReportService salesReportService;

    @GetMapping("/salesReport")
    public String viewReport(Model model){

        List<Order> orders=orderService.findALlOrders();
        model.addAttribute("orders",orders);
        model.addAttribute("size",orders.size());

        List<ProductDto> productDtoList=productService.findAllProducts();
        model.addAttribute("size",productDtoList);

        List<Product> allProducts=salesReportService.findAllProducts();
        List<Object[]> productEarnings=salesReportService.findProductsSoldAndEarnings();

        model.addAttribute("productEarnings", productEarnings);
        model.addAttribute("size",productEarnings.size());
        model.addAttribute("title","Sales Report");

        long totalQuantity = 0;
        double totalRevenue = 0.0;


        for (Object[] productEarning : productEarnings) {
            long quantitySold = ((Number) productEarning[4]).longValue();
            double costPrice=((Number)productEarning[3]).doubleValue();
            double revenue = quantitySold*costPrice;

            totalQuantity += quantitySold;
            totalRevenue += revenue;
        }

        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("totalRevenue", totalRevenue);

        return "salesReport";
    }

    @GetMapping("/salesReport/filter")
    public String filterProducts(@RequestParam(name = "month") int selectedMonth,
                                 @RequestParam(name = "year") int selectedYear,
                                 Model model) {
        List<Object[]> filteredProducts = salesReportService.findProductsSoldAndEarningsFilter(selectedMonth, selectedYear);
        model.addAttribute("filteredProducts", filteredProducts);
        model.addAttribute("size",filteredProducts.size());
        model.addAttribute("title","Sales Report");

        int totalQuantity = 0;
        double totalRevenue = 0.0;

        for (Object[] productEarning : filteredProducts) {
            long quantitySold = ((Number) productEarning[4]).longValue();
            double costPrice=((Number)productEarning[3]).doubleValue();
            double revenue = quantitySold*costPrice;

            totalQuantity += quantitySold;
            totalRevenue += revenue;
        }

        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("totalRevenue", totalRevenue);


        return "salesReportFilter";
    }

}
