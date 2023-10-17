package com.athul.library.service;

import com.athul.library.model.Brand;
import com.athul.library.model.Category;

import java.util.List;

public interface BrandService {


    List<Brand> findAll();

    Brand save(Brand brand);

    Brand getById(Long id);
    Brand update(Brand brand);
    void deleteById(Long id);
    void enableById(Long id);

}
