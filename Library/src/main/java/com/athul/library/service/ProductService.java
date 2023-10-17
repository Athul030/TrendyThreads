package com.athul.library.service;

import com.athul.library.dto.ProductDto;
import com.athul.library.model.Category;
import com.athul.library.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    List<ProductDto> findAll();

    List<ProductDto> findByCategory(Category category);
    List<Product> findProductsByCategory(long id);



    Product save(List<MultipartFile> imageFiles, ProductDto productDto);

//    Product findById(Long id);

    Product update(List<MultipartFile> imageFiles, ProductDto productDto, Long id);

    void deleteById(Long id);

    void enableById(Long id);

    ProductDto getById(Long id);

    Product getProductById(Long id);

    List<ProductDto> searchProducts(String keyword);

    List<ProductDto> searchProductsUserSide(String keyword);

    Page<Product> getProductsPaginated(int page, int pageSize);

    /*Customer*/
    List<Product> getAllProduct();

    List<ProductDto> listViewProduct();

    /* complete later */
    List<Product> getProductsInCategory(Long categoryId);


    List<Product> filterHighPrice();

    List<Product> filterLowPrice();

    List<ProductDto> findAllProducts();

    Product findBYId(long id);



}
