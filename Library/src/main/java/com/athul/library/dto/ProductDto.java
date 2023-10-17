package com.athul.library.dto;

import com.athul.library.model.Brand;
import com.athul.library.model.Category;
import com.athul.library.model.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long id;
    @NotEmpty(message="Field cannot be Empty")
    @NotBlank(message="Field cannot be blank")
    private String name;
    @NotBlank
    @NotEmpty
    private String description;
    @NotEmpty(message="Field cannot be Empty")
    @NotBlank(message="Field cannot be blank")
    private double costPrice;
    @NotEmpty(message="Field cannot be Empty")
    private double salePrice;
    @NotEmpty(message="Field cannot be Empty")
    @NotBlank(message="Field cannot be blank")
    private int currentQuantity;
    @NotEmpty
    private Category category;
    private Brand brand;
    private List<String> imageUrls;

    private boolean productActivated;
    private boolean productDeleted;


//    private String base64Image;


}
