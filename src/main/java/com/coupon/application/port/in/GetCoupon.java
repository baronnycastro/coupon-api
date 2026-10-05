package com.coupon.application.port.in;

import com.coupon.application.dto.CouponOutput;

public interface GetCoupon {

    CouponOutput execute(Long id);
}