package com.athul.library.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDto {

    @Size(min=3,max=10,message = "Invalid first name(3-10 character)")
    @NotEmpty
    @NotBlank(message="Field cannot be blank")
    private String firstName;
    @Size(min=3,max=10,message = "Invalid last name(3-10 character)")
    @NotBlank(message="Field cannot be blank")
    private String lastName;

    @Email
    @NotEmpty
    @NotBlank(message="Field cannot be blank")
    private String username;
    @NotBlank(message="Field cannot be blank")
    @Size(min=3,max=10,message = "Invalid password(5-15 character)")
    @NotEmpty
    private String password;
    @NotBlank(message="Field cannot be blank")
    private String repeatPassword;
}
