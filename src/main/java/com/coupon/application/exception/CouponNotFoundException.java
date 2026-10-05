package com.coupon.application.exception;

public class CouponNotFoundException extends RuntimeException {
    public CouponNotFoundException(Long id) {
        super("Coupon " + id + " not found");
    }
}
