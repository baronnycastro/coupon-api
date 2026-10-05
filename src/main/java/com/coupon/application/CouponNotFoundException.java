package com.coupon.application;

public class CouponNotFoundException extends RuntimeException {
    public CouponNotFoundException(Long id) {
        super("Coupon " + id + " not found");
    }
}
