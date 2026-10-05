package com.coupon.application.port;

import com.coupon.domain.Coupon;

import java.util.Optional;

/** Porta de saida: a camada application so conhece esta interface, nunca JPA. */
public interface CouponRepository {

    Coupon save(Coupon coupon);

    Optional<Coupon> findById(Long id);
}
