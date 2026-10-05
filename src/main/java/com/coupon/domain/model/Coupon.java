package com.coupon.domain.model;

import com.coupon.domain.exception.CouponAlreadyDeletedException;
import com.coupon.domain.exception.InvalidCouponException;
import com.coupon.domain.valueobject.CouponCode;
import com.coupon.domain.valueobject.DiscountValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Agregado de dominio. Todas as regras de negocio de criacao e delecao vivem aqui,
 * sem nenhuma dependencia de framework (nao e a entidade JPA).
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Coupon {

    private final Long id;
    private final CouponCode code;
    private final String description;
    private final DiscountValue discountValue;
    private final Instant expirationDate;
    private final boolean published;
    private final boolean redeemed;
    private CouponStatus status;
    private Instant deletedAt;

    /** Cria um cupom novo aplicando todas as regras de cadastro. */
    public static Coupon create(String code, String description, BigDecimal discountValue,
                                Instant expirationDate, Boolean published, Instant now) {
        CouponCode couponCode = CouponCode.of(code);
        if (description == null || description.isBlank()) {
            throw new InvalidCouponException("description is required");
        }
        DiscountValue discount = DiscountValue.of(discountValue);
        if (expirationDate == null) {
            throw new InvalidCouponException("expirationDate is required");
        }
        if (expirationDate.isBefore(now)) {
            throw new InvalidCouponException("expirationDate cannot be in the past");
        }
        return new Coupon(null, couponCode, description, discount, expirationDate,
                Boolean.TRUE.equals(published), false, CouponStatus.ACTIVE, null);
    }

    /** Reconstitui um cupom ja persistido (sem reaplicar regras de cadastro dependentes do tempo). */
    public static Coupon restore(Long id, String code, String description, BigDecimal discountValue,
                                 Instant expirationDate, boolean published, boolean redeemed,
                                 CouponStatus status, Instant deletedAt) {
        return new Coupon(id, CouponCode.of(code), description, DiscountValue.of(discountValue),
                expirationDate, published, redeemed, status, deletedAt);
    }

    /** Soft delete: marca como removido sem perder nenhum dado do cadastro. */
    public void delete(Instant now) {
        if (isDeleted()) {
            throw new CouponAlreadyDeletedException(id);
        }
        this.status = CouponStatus.DELETED;
        this.deletedAt = now;
    }

    public boolean isDeleted() {
        return status == CouponStatus.DELETED;
    }

    public String getCode() { return code.value(); }
    public BigDecimal getDiscountValue() { return discountValue.value(); }
}
