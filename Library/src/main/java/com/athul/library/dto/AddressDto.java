package com.athul.library.dto;


import com.athul.library.model.Customer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    private Long id;
    @NotEmpty(message = "Address Lines must be filled")
    @NotBlank
    private String address_line_1;
    @NotEmpty(message = "Address Lines must be filled")
    @NotBlank
    private String address_line_2;
    @NotEmpty(message = "City must be filled")
    @NotBlank
    private String city;
    @NotBlank
    @NotEmpty(message = "Pincode must be filled")
    @NotBlank
    private String pincode;
    @NotEmpty(message = "District must be filled")
    @NotBlank
    private String district;
    @NotEmpty(message = "State must be filled")
    @NotBlank
    private String state;
    @NotEmpty(message = "Country must be filled")
    @NotBlank
    private String country;


    private Customer customer;
    private boolean is_default;
}