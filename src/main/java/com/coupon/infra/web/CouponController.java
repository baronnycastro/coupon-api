package com.coupon.infra.web;

import com.coupon.application.CouponOutput;
import com.coupon.application.CreateCouponUseCase;
import com.coupon.application.DeleteCouponUseCase;
import com.coupon.application.GetCouponUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/coupon")
@Tag(name = "coupon")
public class CouponController {

    private final CreateCouponUseCase createCoupon;
    private final GetCouponUseCase getCoupon;
    private final DeleteCouponUseCase deleteCoupon;

    public CouponController(CreateCouponUseCase createCoupon,
                            GetCouponUseCase getCoupon,
                            DeleteCouponUseCase deleteCoupon) {
        this.createCoupon = createCoupon;
        this.getCoupon = getCoupon;
        this.deleteCoupon = deleteCoupon;
    }

    @PostMapping
    @Operation(summary = "Cria um cupom")
    public ResponseEntity<CouponResponse> create(@RequestBody CreateCouponRequest request) {
        CouponOutput output = createCoupon.execute(request.toCommand());
        return ResponseEntity.created(URI.create("/coupon/" + output.id())).body(CouponResponse.from(output));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um cupom pelo id")
    public CouponResponse get(@PathVariable Long id) {
        return CouponResponse.from(getCoupon.execute(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um cupom (soft delete)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteCoupon.execute(id);
        return ResponseEntity.noContent().build();
    }
}
