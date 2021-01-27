package com.notonthehighstreet.service.promotions;

import com.notonthehighstreet.entities.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItemPriceDiscountTest {

    private final Product p1 = new Product("test1", "test1", new BigDecimal("1.00"));
    private final Product p2 = new Product("test2", "test2", new BigDecimal("2.00"));

    @Test
    public void givenNoApplicableProductsForDiscountReturnEmpty() {
        //given
        var basketProducts = Map.of(p1, 1);
        var promotion = new ItemPriceDiscount(p2, 1, new BigDecimal("1.00"));

        //when
        var value = promotion.applyPromo(basketProducts, p1.getPrice());

        //then
        assertEquals(Optional.empty(), value);
    }

    @Test
    public void givenApplicableProductsForDiscountButThresholdNotMetReturnEmpty() {
        //given
        var basketProducts = Map.of(p1, 1, p2, 1);
        var promotion = new ItemPriceDiscount(p2, 2, new BigDecimal("1.00"));

        //when
        var value = promotion.applyPromo(basketProducts, p1.getPrice());

        //then
        assertEquals(Optional.empty(), value);
    }

    @Test
    public void givenApplicableProductsForDiscountAndThresholdMetReturnDetails() {
        //given
        var basketProducts = Map.of(p1, 1, p2, 2);
        var promoThreshold = 2;
        var discount = new BigDecimal("1.00");
        var promotion = new ItemPriceDiscount(p2, promoThreshold, discount);
        var description = "£" + discount.toString() + " off " + p2.getName() + "s when you buy "
                + promoThreshold + " or more";

        //when
        var value = promotion.applyPromo(basketProducts, p1.getPrice());

        //then
        assertTrue(value.isPresent());
        assertEquals(description, value.get().getPromoDescription());
        assertEquals(new BigDecimal("2.00"), value.get().getDiscountToApply());
        assertEquals(2, value.get().getNumberOfTimesApplied());
    }

    @Test
    public void givenApplicableProductsForDiscountAndThresholdExceededReturnDetails() {
        //given
        var basketProducts = Map.of(p1, 1, p2, 3);
        var promoThreshold = 2;
        var discount = new BigDecimal("1.00");
        var promotion = new ItemPriceDiscount(p2, promoThreshold, discount);
        var description = "£" + discount.toString() + " off " + p2.getName() + "s when you buy "
                + promoThreshold + " or more";

        //when
        var value = promotion.applyPromo(basketProducts, p1.getPrice());

        //then
        assertTrue(value.isPresent());
        assertEquals(description, value.get().getPromoDescription());
        assertEquals(new BigDecimal("3.00"), value.get().getDiscountToApply());
        assertEquals(3, value.get().getNumberOfTimesApplied());
    }
}
