package com.coupon.domain.valueobject;

import com.coupon.domain.exception.InvalidCouponException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object do codigo do cupom: alfanumerico, exatamente 6 caracteres.
 * Caracteres especiais sao aceitos na entrada, mas removidos antes de a regra do tamanho ser verificada.
 */
public final class CouponCode {

    public static final int LENGTH = 6;
    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^A-Za-z0-9]");

    private final String value;

    private CouponCode(String value) {
        this.value = value;
    }

    public static CouponCode of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new InvalidCouponException("code is required");
        }
        String sanitized = NON_ALPHANUMERIC.matcher(raw).replaceAll("");
        if (sanitized.length() != LENGTH) {
            throw new InvalidCouponException(
                    "code must have exactly " + LENGTH + " alphanumeric characters after removing special characters");
        }
        return new CouponCode(sanitized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CouponCode other && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
