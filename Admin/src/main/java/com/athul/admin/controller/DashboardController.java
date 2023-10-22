package com.athul.admin.controller;

import com.athul.library.dto.DailyEarnings;
import com.athul.library.dto.DailyEarningsMapping;
import com.athul.library.dto.TotalPriceByPayment;
import com.athul.library.service.DashBoardService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;


@Controller
public class DashboardController {

    private final DashBoardService dashBoardService;

    public DashboardController(DashBoardService dashBoardService) {
        this.dashBoardService = dashBoardService;
    }

    @RequestMapping(value={"/index","/"})
    public String home(Model model, Principal principal) throws JsonProcessingException {
        if (principal == null) {
            return "redirect:/login";
        }
        model.addAttribute("title", "Home Page");
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || authentication instanceof AnonymousAuthenticationToken){
            return "redirect:/login";
        }

        /* Earning card*/
        YearMonth currentYear=YearMonth.now();
        LocalDate localStartDate = LocalDate.of(currentYear.getYear(), currentYear.getMonthValue(), 1);
        LocalDate localEndDate = currentYear.atEndOfMonth();
        Date startDate = java.sql.Date.valueOf(localStartDate);
        Date endDate = java.sql.Date.valueOf(localEndDate);
        double currentMonthEarning=dashBoardService.findCurrentMonthOrder(startDate,endDate);
        Month currentMonth=currentYear.getMonth();
        model.addAttribute("currentMonth",currentMonth);
        model.addAttribute("currentMonthEarning",currentMonthEarning);

        LocalDate localStartDateYearly = LocalDate.of(currentYear.getYear(),Month.JANUARY,1);
        LocalDate localEndDateYearly = LocalDate.of(currentYear.getYear(),Month.DECEMBER,31);
        Date startDateYearly = java.sql.Date.valueOf(localStartDateYearly);
        Date endDateYearly = java.sql.Date.valueOf(localEndDateYearly);
        double currentYearlyEarning=dashBoardService.findCurrentMonthOrder(startDateYearly,endDateYearly);
        int year=currentYear.getYear();
        model.addAttribute("currentYear",year);
        model.addAttribute("currentYearlyEarning",currentYearlyEarning);

        int totalOrders= (int) dashBoardService.findOrdersTotal();
        model.addAttribute("totalOrders",totalOrders);
        int totalPendingOrders= (int) dashBoardService.findOrdersPending();
        model.addAttribute("totalPendingOrders",totalPendingOrders);
        int progress=0;
        if(totalOrders!=0) {
             progress = (totalPendingOrders * 100) / totalOrders;
        }else{
            progress=0;
        }
        model.addAttribute("progress",progress);

        /* Earning chart*/

        int currentYr=currentYear.getYear();
        int currentMnt=currentYear.getMonthValue();
        List<Object[]> dailyEarnings=dashBoardService.retrieveDailyEarnings(currentYr,currentMnt);
        List<DailyEarningsMapping> dailyEarningListForJson=new ArrayList<>();
        for(Object[] obj : dailyEarnings){
            Date date = (Date) obj[0];

            Double amount = (Double) obj[1];
            DailyEarnings dailyEarnings1 = new DailyEarnings(date,amount);
            String input=dailyEarnings1.toString();
            String datePart = input.substring(input.indexOf("date=")+"date=".length(),input.indexOf(" "));
            DailyEarningsMapping dailyEarningsMapping=new DailyEarningsMapping(datePart,amount);
            dailyEarningListForJson.add(dailyEarningsMapping);
        }

        ObjectMapper objectMapper=new ObjectMapper();
        String dailyEarningJson = objectMapper.writeValueAsString(dailyEarningListForJson);
        model.addAttribute("dailyEarnings",dailyEarningJson);

        /* Earning chart End */

        /* Pie chart Start*/
        List<Object[]> priceByPayMethod=dashBoardService.findTotalPricesByPayment();
        List<TotalPriceByPayment> totalPriceByPaymentList=new ArrayList<>();
        for(Object[] obj: priceByPayMethod){
            String payMethod= (String) obj[0];
            Double amount= (Double) obj[1];
            TotalPriceByPayment totalPriceByPayment=new TotalPriceByPayment(payMethod,amount);
            totalPriceByPaymentList.add(totalPriceByPayment);
        }

        ObjectMapper objectMapper1=new ObjectMapper();
        String totalPriceByPayment = objectMapper1.writeValueAsString(totalPriceByPaymentList);
        model.addAttribute("totalPriceByPayment",totalPriceByPayment);



        return "index";
    }
}
