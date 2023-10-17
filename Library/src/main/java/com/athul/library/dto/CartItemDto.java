package com.athul.library.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class CartItemDto {
        private Long id;

        private ShoppingCartDto cart;

        private ProductDto product;

        @NotBlank
        @NotEmpty
        private int quantity;

        @NotBlank
        @NotEmpty
        private double unitPrice;


}
