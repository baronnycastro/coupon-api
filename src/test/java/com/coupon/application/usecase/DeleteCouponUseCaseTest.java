package com.coupon.application.usecase;

import com.coupon.application.exception.CouponNotFoundException;
import com.coupon.domain.model.Coupon;
import com.coupon.domain.exception.CouponAlreadyDeletedException;
import com.coupon.domain.model.CouponStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeleteCouponUseCaseTest {

    private static final Instant NOW = Instant.parse("2026-10-04T12:00:00Z");

    private final InMemoryCouponRepository repository = new InMemoryCouponRepository();
    private final DeleteCouponUseCase useCase =
            new DeleteCouponUseCase(repository, Clock.fixed(NOW, ZoneOffset.UTC));

    private Coupon savedCoupon() {
        return repository.save(Coupon.create("ABC123", "desc", new BigDecimal("0.8"),
                NOW.plus(5, ChronoUnit.DAYS), true, NOW.minusSeconds(3600)));
    }

    @Test
    void softDeletesAndKeepsTheCouponStored() {
        Coupon coupon = savedCoupon();

        useCase.execute(coupon.getId());

        Coupon stored = repository.findById(coupon.getId()).orElseThrow();
        assertThat(stored.getStatus()).isEqualTo(CouponStatus.DELETED);
        assertThat(stored.getDeletedAt()).isEqualTo(NOW);
        assertThat(stored.getDescription()).isEqualTo("desc");
        assertThat(repository.size()).isEqualTo(1);
    }

    @Test
    void cannotDeleteTwice() {
        Coupon coupon = savedCoupon();
        useCase.execute(coupon.getId());

        assertThatThrownBy(() -> useCase.execute(coupon.getId()))
                .isInstanceOf(CouponAlreadyDeletedException.class);
    }

    @Test
    void failsWhenCouponDoesNotExist() {
        assertThatThrownBy(() -> useCase.execute(999L))
                .isInstanceOf(CouponNotFoundException.class);
    }
}
