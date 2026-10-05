package com.coupon.application.usecase;

import com.coupon.application.dto.CouponOutput;
import com.coupon.application.dto.CreateCouponCommand;
import com.coupon.domain.model.CouponStatus;
import com.coupon.domain.exception.InvalidCouponException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateCouponUseCaseTest {

    private static final Instant NOW = Instant.parse("2026-10-04T12:00:00Z");
    private static final Instant FUTURE = NOW.plus(5, ChronoUnit.DAYS);

    private final InMemoryCouponRepository repository = new InMemoryCouponRepository();
    private final CreateCouponUseCase useCase =
            new CreateCouponUseCase(repository, Clock.fixed(NOW, ZoneOffset.UTC));

    @Test
    void persistsCouponWithSanitizedCode() {
        CouponOutput output = useCase.execute(
                new CreateCouponCommand("ABC-123", "desc", new BigDecimal("0.8"), FUTURE, null));

        assertThat(output.code()).isEqualTo("ABC123");
        assertThat(output.status()).isEqualTo(CouponStatus.ACTIVE);
        assertThat(output.published()).isFalse();
        assertThat(output.redeemed()).isFalse();
        assertThat(repository.findById(output.id())).isPresent();
    }

    @Test
    void createsAlreadyPublishedCoupon() {
        CouponOutput output = useCase.execute(
                new CreateCouponCommand("ABC123", "desc", BigDecimal.ONE, FUTURE, true));

        assertThat(output.published()).isTrue();
    }

    @Test
    void usesTheClockToRejectPastExpiration() {
        assertThatThrownBy(() -> useCase.execute(
                new CreateCouponCommand("ABC123", "desc", BigDecimal.ONE, NOW.minusSeconds(1), null)))
                .isInstanceOf(InvalidCouponException.class);
        assertThat(repository.size()).isZero();
    }

    @Test
    void doesNotPersistWhenAnyRuleIsViolated() {
        assertThatThrownBy(() -> useCase.execute(
                new CreateCouponCommand("ABC123", "desc", new BigDecimal("0.4"), FUTURE, null)))
                .isInstanceOf(InvalidCouponException.class);
        assertThat(repository.size()).isZero();
    }
}
