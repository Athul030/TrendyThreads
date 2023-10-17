package com.athul.library.repository;

import com.athul.library.dto.CategoryDto;
import com.athul.library.model.Category;
import com.athul.library.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {


    @Query("select c from Category c where LOWER(c.name) like %?1%")
    List<Category> findAllByName(String keyword);

    @Query("SELECT c from Category c where c.is_activated = true and c.is_deleted = false")
    List<Category> findAllByActivated();

//    @Query("select new com.athul.library.dto.CategoryDto(c.id, c.name, c.is_deleted,c.is_activated) from Category c inner join Product p on p.category.id=c.id where c.is_activated=true and c.is_deleted=false ")
//    List<CategoryDto> getCategoryAndProduct();


    @Query("select p from Product p where p.productActivated=true and p.productDeleted=false order by p.costPrice")
    List<Product> filterLowPrice();

    @Query(value = "select * from categories where is_activated = true", nativeQuery = true)
    List<Category> findAllByActivatedTrue();

}
