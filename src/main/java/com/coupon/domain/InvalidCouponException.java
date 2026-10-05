package com.coupon.domain;

public class InvalidCouponException extends DomainException {
    public InvalidCouponException(String message) {
        super(message);
    }
}
