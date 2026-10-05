package com.coupon.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponTest {

    private static final Instant NOW = Instant.parse("2026-10-04T12:00:00Z");
    private static final Instant FUTURE = NOW.plus(10, ChronoUnit.DAYS);

    private static Coupon coupon(String code, BigDecimal discount, Instant expiration, Boolean published) {
        return Coupon.create(code, "Cupom de teste", discount, expiration, published, NOW);
    }

    private static Coupon validCoupon() {
        return coupon("ABC123", new BigDecimal("0.8"), FUTURE, null);
    }

    // ---------- Create ----------

    @Test
    @DisplayName("novo cupom nasce ACTIVE, nao publicado por padrao e nao resgatado")
    void newCouponDefaults() {
        Coupon coupon = validCoupon();

        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.ACTIVE);
        assertThat(coupon.isPublished()).isFalse();
        assertThat(coupon.isRedeemed()).isFalse();
        assertThat(coupon.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("cupom pode ser criado ja publicado")
    void canBeCreatedPublished() {
        assertThat(coupon("ABC123", BigDecimal.ONE, FUTURE, true).isPublished()).isTrue();
    }

    @Test
    @DisplayName("remove caracteres especiais do codigo antes de guardar")
    void removesSpecialCharactersFromCode() {
        assertThat(coupon("ABC-123", BigDecimal.ONE, FUTURE, null).getCode()).isEqualTo("ABC123");
        assertThat(coupon("A!B@C#1$2%3", BigDecimal.ONE, FUTURE, null).getCode()).isEqualTo("ABC123");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ABC-12", "ABCDEFG", "ABCD-EFG", "!!!!!!", "AB CD"})
    @DisplayName("rejeita codigo que nao tem 6 caracteres alfanumericos apos a limpeza")
    void rejectsCodeWithWrongSizeAfterSanitizing(String code) {
        assertThatThrownBy(() -> coupon(code, BigDecimal.ONE, FUTURE, null))
                .isInstanceOf(InvalidCouponException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("codigo e obrigatorio")
    void codeIsRequired(String code) {
        assertThatThrownBy(() -> coupon(code, BigDecimal.ONE, FUTURE, null))
                .isInstanceOf(InvalidCouponException.class)
                .hasMessageContaining("code");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("descricao e obrigatoria")
    void descriptionIsRequired(String description) {
        assertThatThrownBy(() -> Coupon.create("ABC123", description, BigDecimal.ONE, FUTURE, null, NOW))
                .isInstanceOf(InvalidCouponException.class)
                .hasMessageContaining("description");
    }

    @Test
    @DisplayName("valor de desconto e obrigatorio")
    void discountIsRequired() {
        assertThatThrownBy(() -> coupon("ABC123", null, FUTURE, null))
                .isInstanceOf(InvalidCouponException.class)
                .hasMessageContaining("discountValue");
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.49", "0", "-1"})
    @DisplayName("valor de desconto abaixo de 0,5 e rejeitado")
    void rejectsDiscountBelowMinimum(String discount) {
        assertThatThrownBy(() -> coupon("ABC123", new BigDecimal(discount), FUTURE, null))
                .isInstanceOf(InvalidCouponException.class)
                .hasMessageContaining("discountValue");
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.5", "0.8", "100", "1000000.99"})
    @DisplayName("valor de desconto a partir de 0,5 e aceito, sem maximo")
    void acceptsDiscountFromMinimumWithNoMaximum(String discount) {
        Coupon coupon = coupon("ABC123", new BigDecimal(discount), FUTURE, null);

        assertThat(coupon.getDiscountValue()).isEqualByComparingTo(discount);
    }

    @Test
    @DisplayName("data de expiracao e obrigatoria")
    void expirationIsRequired() {
        assertThatThrownBy(() -> coupon("ABC123", BigDecimal.ONE, null, null))
                .isInstanceOf(InvalidCouponException.class)
                .hasMessageContaining("expirationDate");
    }

    @Test
    @DisplayName("nunca cria cupom com data de expiracao no passado")
    void rejectsExpirationInThePast() {
        assertThatThrownBy(() -> coupon("ABC123", BigDecimal.ONE, NOW.minusSeconds(1), null))
                .isInstanceOf(InvalidCouponException.class)
                .hasMessageContaining("past");
    }

    // ---------- Delete ----------

    @Test
    @DisplayName("delete faz soft delete e preserva os dados do cadastro")
    void deleteIsSoftAndKeepsData() {
        Coupon coupon = coupon("ABC-123", new BigDecimal("0.8"), FUTURE, true);

        coupon.delete(NOW);

        assertThat(coupon.isDeleted()).isTrue();
        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.DELETED);
        assertThat(coupon.getDeletedAt()).isEqualTo(NOW);
        assertThat(coupon.getCode()).isEqualTo("ABC123");
        assertThat(coupon.getDescription()).isEqualTo("Cupom de teste");
        assertThat(coupon.getDiscountValue()).isEqualByComparingTo("0.8");
        assertThat(coupon.getExpirationDate()).isEqualTo(FUTURE);
        assertThat(coupon.isPublished()).isTrue();
    }

    @Test
    @DisplayName("nao e possivel deletar um cupom ja deletado")
    void cannotDeleteTwice() {
        Coupon coupon = validCoupon();
        coupon.delete(NOW);

        assertThatThrownBy(() -> coupon.delete(NOW.plusSeconds(60)))
                .isInstanceOf(CouponAlreadyDeletedException.class);
        assertThat(coupon.getDeletedAt()).isEqualTo(NOW);
    }

    @Test
    @DisplayName("cupom pode ser deletado a qualquer momento, inclusive depois de expirar")
    void canBeDeletedAtAnyTime() {
        Coupon coupon = validCoupon();

        coupon.delete(FUTURE.plus(365, ChronoUnit.DAYS));

        assertThat(coupon.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("restore reconstitui um cupom deletado e continua impedindo nova delecao")
    void restoredDeletedCouponCannotBeDeletedAgain() {
        Coupon restored = Coupon.restore(999L, "ABC123", "desc", BigDecimal.ONE,
                FUTURE, false, false, CouponStatus.DELETED, NOW);

        assertThatThrownBy(() -> restored.delete(NOW)).isInstanceOf(CouponAlreadyDeletedException.class);
    }
}
