package com.athul.library.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponDto {

    private Long id;

    @NotBlank
    @NotEmpty
    private String code;

    @NotBlank
    @NotEmpty
    private String description;

    @Min(value =1)
    @Max(value=100000)
    private int count;
    @Min(value = 1)
    @Max(value=100)
    private int offPercentage;


    @Min(value = 1)
    @Max(value=10000000)
    private int maxOff;


    @Min(value = 1)
    @Max(value=10000000)
    private int minOrderAmount;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;


    private boolean enabled;
}
