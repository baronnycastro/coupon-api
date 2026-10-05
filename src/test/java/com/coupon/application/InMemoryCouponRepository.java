package com.coupon.application;

import com.coupon.application.port.CouponRepository;
import com.coupon.domain.Coupon;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/** Fake (nao mock) da porta de saida: tem comportamento real de armazenamento. */
class InMemoryCouponRepository implements CouponRepository {

    private final Map<Long, Coupon> store = new HashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    @Override
    public Coupon save(Coupon coupon) {
        // Se o coupon nao tem ID (novo), gera um
        Long id = coupon.getId();
        if (id == null) {
            // Criar novo coupon com ID gerado
            id = idSequence.getAndIncrement();
            coupon = Coupon.restore(id, coupon.getCode(), coupon.getDescription(),
                    coupon.getDiscountValue(), coupon.getExpirationDate(),
                    coupon.isPublished(), coupon.isRedeemed(),
                    coupon.getStatus(), coupon.getDeletedAt());
        }
        store.put(id, coupon);
        return coupon;
    }

    @Override
    public Optional<Coupon> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    int size() {
        return store.size();
    }
}
