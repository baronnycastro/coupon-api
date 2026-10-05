package com.coupon.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/coupon")
@Tag(name = "coupon")
public interface CouponHttpApi {

    @PostMapping
    @Operation(summary = "Cria um cupom")
    ResponseEntity<CouponResponse> create(@RequestBody CreateCouponRequest request);

    @GetMapping("/{id}")
    @Operation(summary = "Busca um cupom pelo id")
    CouponResponse get(@PathVariable Long id);

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um cupom (soft delete)")
    ResponseEntity<Void> delete(@PathVariable Long id);
}