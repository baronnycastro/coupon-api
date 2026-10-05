package com.coupon;

import com.coupon.domain.model.CouponStatus;
import com.coupon.adapter.out.persistence.CouponJpaEntity;
import com.coupon.adapter.out.persistence.SpringDataCouponRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Testa o comportamento ponta a ponta, com banco H2 real (sem mocks). */
@SpringBootTest
@AutoConfigureMockMvc
class CouponApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SpringDataCouponRepository jpaRepository;

    private Map<String, Object> validBody() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", "ABC-123");
        body.put("description", "Cupom de teste");
        body.put("discountValue", 0.8);
        body.put("expirationDate", Instant.now().plus(1, ChronoUnit.DAYS).toString());
        body.put("published", false);
        return body;
    }

    private ResultActions postCoupon(Map<String, Object> body) throws Exception {
        return mockMvc.perform(post("/coupon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    private String createCouponAndGetId() throws Exception {
        String json = postCoupon(validBody()).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(json).get("id").asText();
    }

    // ---------- Create ----------

    @Test
    void createReturns201WithSanitizedCodeAndDefaults() throws Exception {
        postCoupon(validBody())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.code").value("ABC123"))
                .andExpect(jsonPath("$.discountValue").value(0.8))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.published").value(false))
                .andExpect(jsonPath("$.redeemed").value(false));
    }

    @Test
    void sanitizesCodeBeforeReturningAndPersisting() throws Exception {
        Map<String, Object> body = validBody();
        body.put("code", "A!B@C#1$2%3");

        String response = postCoupon(body)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("ABC123"))
                .andReturn().getResponse().getContentAsString();
        String id = objectMapper.readTree(response).get("id").asText();

        CouponJpaEntity stored = jpaRepository.findById(Long.parseLong(id)).orElseThrow();
        assertThat(stored.getCode()).isEqualTo("ABC123");
    }

    @Test
    void createsAlreadyPublishedCoupon() throws Exception {
        Map<String, Object> body = validBody();
        body.put("published", true);

        postCoupon(body).andExpect(status().isCreated()).andExpect(jsonPath("$.published").value(true));
    }

    @Test
    void rejectsExpirationDateInThePast() throws Exception {
        Map<String, Object> body = validBody();
        body.put("expirationDate", Instant.now().minus(1, ChronoUnit.DAYS).toString());

        postCoupon(body).andExpect(status().isBadRequest());
    }

    @Test
    void rejectsDiscountBelowMinimum() throws Exception {
        Map<String, Object> body = validBody();
        body.put("discountValue", 0.49);

        postCoupon(body).andExpect(status().isBadRequest());
    }

    @Test
    void acceptsMinimumDiscount() throws Exception {
        Map<String, Object> body = validBody();
        body.put("discountValue", 0.5);

        postCoupon(body).andExpect(status().isCreated()).andExpect(jsonPath("$.discountValue").value(0.5));
    }

    @Test
    void acceptsLargeDiscountWithoutBusinessMaximum() throws Exception {
        BigDecimal discount = new BigDecimal("999999999999999999999999999.99");
        Map<String, Object> body = validBody();
        body.put("discountValue", discount);

        String response = postCoupon(body)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String id = objectMapper.readTree(response).get("id").asText();

        assertThat(response).contains("\"discountValue\":" + discount.toPlainString());
        assertThat(jpaRepository.findById(Long.parseLong(id)).orElseThrow().getDiscountValue())
                .isEqualByComparingTo(discount);
    }

    @Test
    void rejectsCodeThatIsNotSixCharactersAfterSanitizing() throws Exception {
        Map<String, Object> body = validBody();
        body.put("code", "ABC-12");

        postCoupon(body).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"code", "description", "discountValue", "expirationDate"})
    void rejectsMissingRequiredFields(String field) throws Exception {
        Map<String, Object> body = validBody();
        body.remove(field);

        postCoupon(body).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"code", "description", "discountValue", "expirationDate"})
    void rejectsNullRequiredFields(String field) throws Exception {
        Map<String, Object> body = validBody();
        body.put(field, null);

        postCoupon(body).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"code", "description"})
    void rejectsBlankRequiredTextFields(String field) throws Exception {
        Map<String, Object> body = validBody();
        body.put(field, "   ");

        postCoupon(body).andExpect(status().isBadRequest());
    }

    @Test
    void rejectsMalformedBody() throws Exception {
        mockMvc.perform(post("/coupon").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest());
    }

    // ---------- Get ----------

    @Test
    void getReturnsTheCoupon() throws Exception {
        String id = createCouponAndGetId();

        mockMvc.perform(get("/coupon/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.code").value("ABC123"));
    }

    @Test
    void getUnknownCouponReturns404() throws Exception {
        mockMvc.perform(get("/coupon/999999")).andExpect(status().isNotFound());
    }

    @Test
    void getWithInvalidIdReturns400() throws Exception {
        mockMvc.perform(get("/coupon/not-a-uuid")).andExpect(status().isBadRequest());
    }

    // ---------- Delete ----------

    @Test
    void deleteReturns204AndKeepsTheDataInTheDatabase() throws Exception {
        String id = createCouponAndGetId();

        mockMvc.perform(delete("/coupon/" + id)).andExpect(status().isNoContent());

        CouponJpaEntity stored = jpaRepository.findById(Long.parseLong(id)).orElseThrow();
        assertThat(stored.getStatus()).isEqualTo(CouponStatus.DELETED);
        assertThat(stored.getDeletedAt()).isNotNull();
        assertThat(stored.getCode()).isEqualTo("ABC123");
        assertThat(stored.getDescription()).isEqualTo("Cupom de teste");
        assertThat(stored.getDiscountValue()).isEqualByComparingTo("0.8");
    }

    @Test
    void canDeleteAnExpiredCouponThroughEndpoint() throws Exception {
        String id = createCouponAndGetId();
        CouponJpaEntity stored = jpaRepository.findById(Long.parseLong(id)).orElseThrow();
        stored.setExpirationDate(Instant.now().minus(1, ChronoUnit.DAYS));
        jpaRepository.saveAndFlush(stored);

        mockMvc.perform(delete("/coupon/" + id)).andExpect(status().isNoContent());

        CouponJpaEntity deleted = jpaRepository.findById(Long.parseLong(id)).orElseThrow();
        assertThat(deleted.getStatus()).isEqualTo(CouponStatus.DELETED);
        assertThat(deleted.getDeletedAt()).isNotNull();
        assertThat(deleted.getExpirationDate()).isBefore(Instant.now());
    }

    @Test
    void cannotDeleteTheSameCouponTwice() throws Exception {
        String id = createCouponAndGetId();

        mockMvc.perform(delete("/coupon/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(delete("/coupon/" + id)).andExpect(status().isConflict());
    }

    @Test
    void deleteUnknownCouponReturns404() throws Exception {
        mockMvc.perform(delete("/coupon/999999")).andExpect(status().isNotFound());
    }

    @Test
    void getAfterDeleteShowsDeletedStatus() throws Exception {
        String id = createCouponAndGetId();
        mockMvc.perform(delete("/coupon/" + id)).andExpect(status().isNoContent());

        mockMvc.perform(get("/coupon/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELETED"));
    }
}
