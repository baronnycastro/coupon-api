package com.coupon.application;

import com.coupon.application.port.CouponRepository;
import com.coupon.domain.Coupon;

import java.time.Clock;

/** Orquestra a criacao. As regras de negocio estao em {@link Coupon}. */
public class CreateCouponUseCase {

    private final CouponRepository repository;
    private final Clock clock;

    public CreateCouponUseCase(CouponRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public CouponOutput execute(CreateCouponCommand command) {
        Coupon coupon = Coupon.create(
                command.code(),
                command.description(),
                command.discountValue(),
                command.expirationDate(),
                command.published(),
                clock.instant());
        return CouponOutput.from(repository.save(coupon));
    }
}
