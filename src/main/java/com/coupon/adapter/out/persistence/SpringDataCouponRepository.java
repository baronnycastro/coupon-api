package com.coupon.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCouponRepository extends JpaRepository<CouponJpaEntity, Long> {
}
