package com.coupon.infra.persistence;

import com.coupon.domain.Coupon;

final class CouponPersistenceMapper {

    private CouponPersistenceMapper() {
    }

    static CouponJpaEntity toEntity(Coupon coupon) {
        return new CouponJpaEntity(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDescription(),
                coupon.getDiscountValue(),
                coupon.getExpirationDate(),
                coupon.getStatus(),
                coupon.isPublished(),
                coupon.isRedeemed(),
                coupon.getDeletedAt());
    }

    static Coupon toDomain(CouponJpaEntity entity) {
        return Coupon.restore(
                entity.getId(),
                entity.getCode(),
                entity.getDescription(),
                entity.getDiscountValue(),
                entity.getExpirationDate(),
                entity.isPublished(),
                entity.isRedeemed(),
                entity.getStatus(),
                entity.getDeletedAt());
    }
}
