package com.athul.library.service;

import com.athul.library.dto.DailyEarnings;

import java.time.Month;
import java.time.YearMonth;
import java.util.Date;
import java.util.List;

public interface DashBoardService {
    double findCurrentMonthOrder(Date startDate, Date endDate );

    long findOrdersTotal();

    int findOrdersPending();

    List<Object[]> retrieveDailyEarnings( int currentYr, int currentMnt);

    List<Object[]> findTotalPricesByPayment();
}
