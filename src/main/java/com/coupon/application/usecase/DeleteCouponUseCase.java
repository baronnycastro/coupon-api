package com.coupon.application.usecase;

import com.coupon.application.exception.CouponNotFoundException;
import com.coupon.application.port.in.DeleteCoupon;
import com.coupon.application.port.out.CouponRepository;
import com.coupon.domain.model.Coupon;

import java.time.Clock;

/** Orquestra o soft delete. A regra "nao deletar duas vezes" vive em {@link Coupon#delete}. */
public class DeleteCouponUseCase implements DeleteCoupon {

    private final CouponRepository repository;
    private final Clock clock;

    public DeleteCouponUseCase(CouponRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    public void execute(Long id) {
        Coupon coupon = repository.findById(id).orElseThrow(() -> new CouponNotFoundException(id));
        coupon.delete(clock.instant());
        repository.save(coupon);
    }
}
