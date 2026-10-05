package com.coupon.application;

import com.coupon.domain.Coupon;
import com.coupon.domain.CouponStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record CouponOutput(
        Long id,
        String code,
        String description,
        BigDecimal discountValue,
        Instant expirationDate,
        CouponStatus status,
        boolean published,
        boolean redeemed) {

    public static CouponOutput from(Coupon coupon) {
        return new CouponOutput(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDescription(),
                coupon.getDiscountValue(),
                coupon.getExpirationDate(),
                coupon.getStatus(),
                coupon.isPublished(),
                coupon.isRedeemed());
    }
}
