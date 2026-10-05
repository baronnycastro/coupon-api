package com.coupon.application.port.in;

import com.coupon.application.dto.CouponOutput;
import com.coupon.application.dto.CreateCouponCommand;

public interface CreateCoupon {

    CouponOutput execute(CreateCouponCommand command);
}