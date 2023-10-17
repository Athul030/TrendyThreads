package com.athul.library.dto;


import com.athul.library.model.Customer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCartDto {


        private Long id;

        private Customer customer;

        @NotBlank
        @NotEmpty
        private double totalPrice;

        @NotBlank
        @NotEmpty
        private int totalItems;

        private Set<CartItemDto> cartItems;


}
