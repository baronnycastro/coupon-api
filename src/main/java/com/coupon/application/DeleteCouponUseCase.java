package com.coupon.application;

import com.coupon.application.port.CouponRepository;
import com.coupon.domain.Coupon;

import java.time.Clock;

/** Orquestra o soft delete. A regra "nao deletar duas vezes" vive em {@link Coupon#delete}. */
public class DeleteCouponUseCase {

    private final CouponRepository repository;
    private final Clock clock;

    public DeleteCouponUseCase(CouponRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public void execute(Long id) {
        Coupon coupon = repository.findById(id).orElseThrow(() -> new CouponNotFoundException(id));
        coupon.delete(clock.instant());
        repository.save(coupon);
    }
}
