package com.athul.library.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    private Long id;
    @NotEmpty(message="Field cannot be Empty")
    @NotBlank(message="Field cannot be blank")
    private String name;
    private boolean is_deleted;
    private boolean is_activated;
}
