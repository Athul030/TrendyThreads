package com.athul.library.service;

import com.athul.library.dto.CategoryDto;
import com.athul.library.dto.ProductDto;
import com.athul.library.model.Category;

import java.util.List;

public interface CategoryService {

    List<Category> findAll();

    Category save(Category category);

    Category findById(Long id);
    Category update(Category category);
    void deleteById(Long id);
    void enabledById(Long id);

    List<Category> findAllByActivated();

    List<CategoryDto> searchCategories(String keyword);

    List<CategoryDto> getCategoryAndProduct();

    List<Category> findAllByActivatedTrue();

}
