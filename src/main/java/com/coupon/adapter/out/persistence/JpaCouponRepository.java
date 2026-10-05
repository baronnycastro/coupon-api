package com.coupon.adapter.out.persistence;

import com.coupon.application.port.out.CouponRepository;
import com.coupon.domain.model.Coupon;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/** Adapter: implementa a porta da camada application usando Spring Data JPA. */
@Repository
public class JpaCouponRepository implements CouponRepository {

    private final SpringDataCouponRepository jpa;

    public JpaCouponRepository(SpringDataCouponRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional
    public Coupon save(Coupon coupon) {
        return CouponPersistenceMapper.toDomain(jpa.save(CouponPersistenceMapper.toEntity(coupon)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Coupon> findById(Long id) {
        return jpa.findById(id).map(CouponPersistenceMapper::toDomain);
    }
}
