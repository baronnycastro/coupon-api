package com.coupon.adapter.in.web;

import com.coupon.application.dto.CreateCouponCommand;

import java.math.BigDecimal;
import java.time.Instant;

/** Sem validacao aqui de proposito: as regras de negocio sao do dominio. */
public record CreateCouponRequest(
        String code,
        String description,
        BigDecimal discountValue,
        Instant expirationDate,
        Boolean published) {

    CreateCouponCommand toCommand() {
        return new CreateCouponCommand(code, description, discountValue, expirationDate, published);
    }
}
