package com.coupon.infra.web;

import com.coupon.application.CouponOutput;
import com.coupon.domain.CouponStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record CouponResponse(
        Long id,
        String code,
        String description,
        BigDecimal discountValue,
        Instant expirationDate,
        CouponStatus status,
        boolean published,
        boolean redeemed) {

    static CouponResponse from(CouponOutput output) {
        return new CouponResponse(
                output.id(),
                output.code(),
                output.description(),
                output.discountValue(),
                output.expirationDate(),
                output.status(),
                output.published(),
                output.redeemed());
    }
}
