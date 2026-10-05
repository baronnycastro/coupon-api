package com.coupon.bootstrap;

import com.coupon.application.usecase.CreateCouponUseCase;
import com.coupon.application.usecase.DeleteCouponUseCase;
import com.coupon.application.usecase.GetCouponUseCase;
import com.coupon.application.port.in.CreateCoupon;
import com.coupon.application.port.in.DeleteCoupon;
import com.coupon.application.port.in.GetCoupon;
import com.coupon.application.port.out.CouponRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/** A camada application nao tem anotacoes Spring: a montagem dos beans fica no bootstrap. */
@Configuration
public class UseCaseConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public CreateCoupon createCouponUseCase(CouponRepository repository, Clock clock) {
        return new CreateCouponUseCase(repository, clock);
    }

    @Bean
    public GetCoupon getCouponUseCase(CouponRepository repository) {
        return new GetCouponUseCase(repository);
    }

    @Bean
    public DeleteCoupon deleteCouponUseCase(CouponRepository repository, Clock clock) {
        return new DeleteCouponUseCase(repository, clock);
    }
}
