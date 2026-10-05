package com.coupon.application;

import com.coupon.application.port.CouponRepository;

public class GetCouponUseCase {

    private final CouponRepository repository;

    public GetCouponUseCase(CouponRepository repository) {
        this.repository = repository;
    }

    public CouponOutput execute(Long id) {
        return repository.findById(id)
                .map(CouponOutput::from)
                .orElseThrow(() -> new CouponNotFoundException(id));
    }
}
