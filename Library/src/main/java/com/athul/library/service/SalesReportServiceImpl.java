package com.athul.library.service;


import com.athul.library.model.OrderDetail;
import com.athul.library.model.Product;
import com.athul.library.repository.OrderDetailRepository;
import com.athul.library.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SalesReportServiceImpl implements SalesReportService{

    private ProductRepository productRepository;

    private OrderDetailRepository orderDetailRepository;


    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Object[]> findProductsSoldAndEarnings() {
        return orderDetailRepository.findProductsSoldAndRevenue();
    }

    @Override
    public List<Object[]> findProductsSoldAndEarningsFilter(int month, int year) {
        return orderDetailRepository.findProductsSoldAndRevenueByMonthAndYear(month,year);
    }
}
