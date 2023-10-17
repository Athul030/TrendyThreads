package com.athul.library.repository;

import com.athul.library.model.Order;
import com.athul.library.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail,Long> {
    @Query("select o from Order o where o.customer.id = ?1")
    List<Order> findAllByCustomerId(Long id);

    @Query("SELECT p.id, p.name, p.category.name, p.costPrice, SUM(od.quantity), SUM(od.totalPrice) " +
            "FROM OrderDetail od " +
            "INNER JOIN od.product p " +
            "GROUP BY p.id, p.name, p.category, p.costPrice")
    List<Object[]> findProductsSoldAndRevenue();


    @Query("SELECT p.id, p.name, p.category.name, p.costPrice, SUM(od.quantity), SUM(od.totalPrice) " +
            "FROM OrderDetail od " +
            "INNER JOIN od.product p " +
            "INNER JOIN od.order o " +
            "WHERE YEAR(o.orderDate) = :year AND MONTH(o.orderDate) = :month " +
            "GROUP BY p.id, p.name, p.category, p.costPrice" +
            " ORDER BY p.id")
    List<Object[]> findProductsSoldAndRevenueByMonthAndYear(@Param("month") int month, @Param("year") int year);


}
