package com.athul.library.dto;

import lombok.Data;

@Data
public class DailyEarningsMapping {

    private String date;
    private Double earnings;

    public DailyEarningsMapping(String date, Double earnings) {
        this.date = date;
        this.earnings = earnings;
    }

}
