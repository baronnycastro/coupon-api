package com.coupon.domain;

public class CouponAlreadyDeletedException extends DomainException {
    public CouponAlreadyDeletedException(Long id) {
        super("Coupon " + id + " is already deleted");
    }
}
