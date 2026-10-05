package com.coupon.infra.config;

import com.coupon.application.CreateCouponUseCase;
import com.coupon.application.DeleteCouponUseCase;
import com.coupon.application.GetCouponUseCase;
import com.coupon.application.port.CouponRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/** A camada application nao tem anotacoes Spring: a montagem dos beans fica na infra. */
@Configuration
public class UseCaseConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public CreateCouponUseCase createCouponUseCase(CouponRepository repository, Clock clock) {
        return new CreateCouponUseCase(repository, clock);
    }

    @Bean
    public GetCouponUseCase getCouponUseCase(CouponRepository repository) {
        return new GetCouponUseCase(repository);
    }

    @Bean
    public DeleteCouponUseCase deleteCouponUseCase(CouponRepository repository, Clock clock) {
        return new DeleteCouponUseCase(repository, clock);
    }
}
