package com.notonthehighstreet;

import com.notonthehighstreet.entities.Product;
import com.notonthehighstreet.service.Checkout;
import com.notonthehighstreet.service.promotions.ItemPriceDiscount;
import com.notonthehighstreet.service.promotions.Promotion;
import com.notonthehighstreet.service.promotions.TenPercentBasketDiscount;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CheckoutItTest {

    private final Product travelCard = new Product("001", "Travel Card Holder",
            new BigDecimal("9.25"));
    private final Product cuffLinks = new Product("002", "Personalised cufflinks",
            new BigDecimal("45.00"));
    private final Product kidsTshirt = new Product("003", "Kids T-shirt",
            new BigDecimal("19.95"));
    private final List<Promotion> promos = List.of(
            new ItemPriceDiscount(travelCard, 2, new BigDecimal("0.75")),
            new TenPercentBasketDiscount(new BigDecimal("60.00")));

    @Test
    public void testScenario1() {
        //given
        var checkout = new Checkout(promos);
        checkout.scan(travelCard);
        checkout.scan(cuffLinks);
        checkout.scan(kidsTshirt);

        //when
        var result = checkout.total();

        //then
        assertEquals(66.78, result);
    }

    @Test
    public void testScenario2() {
        //given
        var checkout = new Checkout(promos);
        checkout.scan(travelCard);
        checkout.scan(kidsTshirt);
        checkout.scan(travelCard);

        //when
        var result = checkout.total();

        //then
        assertEquals(36.95, result);
    }

    @Test
    public void testScenario3() {
        //given
        var checkout = new Checkout(promos);
        checkout.scan(travelCard);
        checkout.scan(cuffLinks);
        checkout.scan(travelCard);
        checkout.scan(kidsTshirt);

        //when
        var result = checkout.total();

        //then
        assertEquals(73.76, result);
    }

}
