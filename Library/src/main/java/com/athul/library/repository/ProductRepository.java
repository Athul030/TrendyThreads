package com.athul.library.repository;

import com.athul.library.dto.ProductDto;
import com.athul.library.model.Category;
import com.athul.library.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findById(long id);

    List<Product> findAllByCategory(Category category);

    List<Product> findAllByCategoryId(long id);

    @Query(value = "SELECT p.*,c.category_id as cat_id FROM products p JOIN categories c ON p.category_id=c.category_id WHERE p.product_Deleted=FALSE AND c.is_deleted=FALSE ",nativeQuery = true)
    Page<Product> findAllPageable(Pageable pageable);


    @Query("select p from Product p where LOWER(p.description) like %?1% or LOWER(p.name) like %?1%")
    List<Product> findAllByNameOrDescription(String keyword);  //Page for pagination instead of List


    @Query("select p from Product p where p.productActivated=true and p.productDeleted=false")
    List<Product> getAllProducts();

    @Query(value = "SELECT p.*,c.category_id as cat_id FROM products p JOIN categories c ON p.category_id=c.category_id WHERE p.product_Deleted=FALSE AND c.is_deleted=FALSE ",nativeQuery = true)
    List<Product> listViewProducts();

    @Query(value = "SELECT p.*,c.category_id as cat_id FROM products p JOIN categories c ON p.category_id=c.category_id WHERE p.product_Deleted=FALSE AND c.is_deleted=FALSE AND (LOWER(p.name) LIKE %:keyword% OR LOWER(p.description) LIKE %:keyword%) ",nativeQuery = true)
    List<Product> listViewProductsUserSide(String keyword);



    @Query("select p from Product p")
    Page<Product> pageProduct(Pageable pageable);

//    @Query(value = "select * from Product where is_activated = true", nativeQuery = true)
//    List<Product> findAllByActivatedTrue();

    @Query(value = "select * from products where products.product_activated = true", nativeQuery = true)
    List<Product> findAllByActivatedTrue();

//    @Query("SELECT p.imageUrls FROM Product p WHERE p.id = :productId")
//    List<String> findImageUrlsByProductId(@Param("productId") Long productId);

//    @Query("SELECT p.imageUrls FROM Product p")
//    List<String> findImageUrlsByProductId(@Param("productId") Long productId);

//    @Query("select p from Product p where p.productActivated=true and p.productDeleted=false order by p.costPrice")
//    List<Product> filterLowPrice();

    @Query(value = "SELECT p.*,c.category_id as cat_id FROM products p JOIN categories c ON p.category_id=c.category_id WHERE p.product_Deleted=FALSE AND c.is_deleted=FALSE ORDER BY p.cost_Price",nativeQuery = true)
    List<Product> filterLowPrice();
    @Query(value = "SELECT p.*,c.category_id as cat_id FROM products p JOIN categories c ON p.category_id=c.category_id WHERE p.product_Deleted=FALSE AND c.is_deleted=FALSE ORDER BY p.cost_Price DESC ",nativeQuery = true)
    List<Product> filterHighPrice();

    List<Product> findAll();

}
