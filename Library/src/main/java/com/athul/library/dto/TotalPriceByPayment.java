package com.athul.library.dto;

import lombok.Data;


@Data
public class TotalPriceByPayment {

    private String payMethod;
    private Double amount;

    public TotalPriceByPayment(String payMethod, Double amount) {
        this.payMethod = payMethod;
        this.amount = amount;
    }
}
