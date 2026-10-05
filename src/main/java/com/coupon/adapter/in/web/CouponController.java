package com.coupon.adapter.in.web;

import com.coupon.application.dto.CouponOutput;
import com.coupon.application.port.in.CreateCoupon;
import com.coupon.application.port.in.DeleteCoupon;
import com.coupon.application.port.in.GetCoupon;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class CouponController implements CouponHttpApi {

    private final CreateCoupon createCoupon;
    private final GetCoupon getCoupon;
    private final DeleteCoupon deleteCoupon;

    public CouponController(CreateCoupon createCoupon,
                            GetCoupon getCoupon,
                            DeleteCoupon deleteCoupon) {
        this.createCoupon = createCoupon;
        this.getCoupon = getCoupon;
        this.deleteCoupon = deleteCoupon;
    }

    @Override
    public ResponseEntity<CouponResponse> create(CreateCouponRequest request) {
        CouponOutput output = createCoupon.execute(request.toCommand());
        return ResponseEntity.created(URI.create("/coupon/" + output.id())).body(CouponResponse.from(output));
    }

    @Override
    public CouponResponse get(Long id) {
        return CouponResponse.from(getCoupon.execute(id));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        deleteCoupon.execute(id);
        return ResponseEntity.noContent().build();
    }
}
