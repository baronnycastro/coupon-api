package com.coupon.domain.valueobject;

import com.coupon.domain.exception.InvalidCouponException;

import java.math.BigDecimal;

/**
 * Value Object do valor de desconto: saldo absoluto, minimo 0,5, sem maximo.
 */
public final class DiscountValue {

    public static final BigDecimal MINIMUM = new BigDecimal("0.5");

    private final BigDecimal value;

    private DiscountValue(BigDecimal value) {
        this.value = value;
    }

    public static DiscountValue of(BigDecimal raw) {
        if (raw == null) {
            throw new InvalidCouponException("discountValue is required");
        }
        if (raw.compareTo(MINIMUM) < 0) {
            throw new InvalidCouponException("discountValue must be at least " + MINIMUM.toPlainString());
        }
        return new DiscountValue(raw.stripTrailingZeros());
    }

    public BigDecimal value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DiscountValue other && value.compareTo(other.value) == 0;
    }

    @Override
    public int hashCode() {
        return value.stripTrailingZeros().hashCode();
    }
}
