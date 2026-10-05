package com.coupon.adapter.out.persistence;

import com.coupon.domain.model.CouponStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

/** Modelo de persistencia. Nao contem regra de negocio: ela vive no dominio. */
@Entity
@Table(name = "coupon")
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class CouponJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 6)
    private String code;

    @Column(nullable = false, length = 10000)
    private String description;

    @Column(name = "discount_value", nullable = false, precision = 38, scale = 10)
    private BigDecimal discountValue;

    @Column(name = "expiration_date", nullable = false)
    private Instant expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CouponStatus status;

    @Column(nullable = false)
    private boolean published;

    @Column(nullable = false)
    private boolean redeemed;

    @Column(name = "deleted_at")
    private Instant deletedAt;

}
