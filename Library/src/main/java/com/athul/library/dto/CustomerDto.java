package com.athul.library.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto {

    @NotBlank(message="Field cannot be blank")
    @NotEmpty
    @Size(min=3,max = 15,message ="first name should have 3-15 characters" )
    private String firstName;

    @NotBlank(message="Field cannot be blank")
    @NotEmpty
    @Size(min=3,max = 15,message ="last name should have 3-15 characters" )
    private String lastName;

    @NotBlank(message="Field cannot be blank")
    @NotEmpty
    @Email
    private String username;

    @NotBlank(message="Field cannot be blank")
    @NotEmpty
    @Size(min=3,max = 20,message ="password should have 3-20 characters" )
    private String password;

    @NotBlank(message="Field cannot be blank")
    @NotEmpty
    @Size(min=10,max = 10,message ="Mobile number should have 10 digits" )
    private String phoneNumber;

    @NotBlank(message="Field cannot be blank")
    @NotEmpty
    private String repeatPassword;

    private boolean is_blocked;

    private long otp;

    private String referralCode;

}
