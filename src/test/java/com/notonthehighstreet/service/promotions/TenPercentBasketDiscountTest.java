package com.notonthehighstreet.service.promotions;

import com.notonthehighstreet.entities.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TenPercentBasketDiscountTest {

    private final Product p1 = new Product("test1", "test1", new BigDecimal("1.00"));

    @Test
    public void givenBasketThatDoesNotMeetThresholdReturnEmpty() {
        //given
        var productCount = 1;
        var basketProducts = Map.of(p1, productCount);
        var promotion = new TenPercentBasketDiscount(new BigDecimal("2.00"));

        //when
        var value = promotion.applyPromo(basketProducts, p1.getPrice());

        //then
        assertEquals(Optional.empty(), value);
    }

    @Test
    public void givenBasketThatEqualsThresholdReturnEmpty() {
        //given
        var productCount = 2;
        var basketProducts = Map.of(p1, productCount);
        var promotion = new TenPercentBasketDiscount(new BigDecimal("2.00"));

        //when
        var value = promotion.applyPromo(basketProducts,
                p1.getPrice().multiply(new BigDecimal(productCount)));

        //then
        assertEquals(Optional.empty(), value);
    }

    @Test
    public void givenBasketThatExceedsThresholdReturnDetails() {
        //given
        var productCount = 3;
        var basketProducts = Map.of(p1, productCount);
        var promoThreshold = new BigDecimal("2.00");
        var promotion = new TenPercentBasketDiscount(promoThreshold);
        var description = "10% discount on carts over £" + promoThreshold.toString();

        //when
        var value = promotion.applyPromo(basketProducts,
                p1.getPrice().multiply(new BigDecimal(productCount)));

        //then
        assertTrue(value.isPresent());
        assertEquals(description, value.get().getPromoDescription());
        assertEquals(new BigDecimal("0.300"), value.get().getDiscountToApply());
        assertEquals(1, value.get().getNumberOfTimesApplied());
    }
}
