package com.athul.library.repository;

import com.athul.library.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Coupon findCouponByCode(String code);

    Coupon findById(long id);

}
