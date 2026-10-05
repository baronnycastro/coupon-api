package com.coupon.application.usecase;

import com.coupon.application.exception.CouponNotFoundException;
import com.coupon.domain.model.Coupon;
import com.coupon.domain.model.CouponStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetCouponUseCaseTest {

    private static final Instant NOW = Instant.parse("2026-10-04T12:00:00Z");

    private final InMemoryCouponRepository repository = new InMemoryCouponRepository();
    private final GetCouponUseCase useCase = new GetCouponUseCase(repository);

    @Test
    void returnsStoredCoupon() {
        Coupon coupon = repository.save(Coupon.create("ABC123", "desc", BigDecimal.ONE,
                NOW.plus(1, ChronoUnit.DAYS), false, NOW));

        assertThat(useCase.execute(coupon.getId()).code()).isEqualTo("ABC123");
    }

    @Test
    void returnsDeletedCouponWithDeletedStatus() {
        Coupon coupon = repository.save(Coupon.create("ABC123", "desc", BigDecimal.ONE,
                NOW.plus(1, ChronoUnit.DAYS), false, NOW));
        coupon.delete(NOW);

        assertThat(useCase.execute(coupon.getId()).status()).isEqualTo(CouponStatus.DELETED);
    }

    @Test
    void failsWhenCouponDoesNotExist() {
        assertThatThrownBy(() -> useCase.execute(999L))
                .isInstanceOf(CouponNotFoundException.class);
    }
}
