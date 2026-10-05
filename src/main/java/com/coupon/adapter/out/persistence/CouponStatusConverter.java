package com.coupon.adapter.out.persistence;

import com.coupon.domain.model.CouponStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Conversor JPA: mapeia enum CouponStatus para CHAR(1) no banco
 * ACTIVE → 'A'
 * DELETED → 'D'
 */
@Converter(autoApply = true)
public class CouponStatusConverter implements AttributeConverter<CouponStatus, String> {

    @Override
    public String convertToDatabaseColumn(CouponStatus status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case ACTIVE -> "A";
            case DELETED -> "D";
        };
    }

    @Override
    public CouponStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return switch (dbData) {
            case "A" -> CouponStatus.ACTIVE;
            case "D" -> CouponStatus.DELETED;
            default -> throw new IllegalArgumentException("Status inválido: " + dbData);
        };
    }
}
