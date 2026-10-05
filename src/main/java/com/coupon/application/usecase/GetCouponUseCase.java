package com.coupon.application.usecase;

import com.coupon.application.dto.CouponOutput;
import com.coupon.application.exception.CouponNotFoundException;
import com.coupon.application.port.in.GetCoupon;
import com.coupon.application.port.out.CouponRepository;

public class GetCouponUseCase implements GetCoupon {

    private final CouponRepository repository;

    public GetCouponUseCase(CouponRepository repository) {
        this.repository = repository;
    }

    @Override
    public CouponOutput execute(Long id) {
        return repository.findById(id)
                .map(CouponOutput::from)
                .orElseThrow(() -> new CouponNotFoundException(id));
    }
}
