package com.coupon.application.usecase;

import com.coupon.application.dto.CouponOutput;
import com.coupon.application.dto.CreateCouponCommand;
import com.coupon.application.port.in.CreateCoupon;
import com.coupon.application.port.out.CouponRepository;
import com.coupon.domain.model.Coupon;

import java.time.Clock;

/** Orquestra a criacao. As regras de negocio estao em {@link Coupon}. */
public class CreateCouponUseCase implements CreateCoupon {

    private final CouponRepository repository;
    private final Clock clock;

    public CreateCouponUseCase(CouponRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
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
