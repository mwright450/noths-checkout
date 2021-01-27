package com.notonthehighstreet.service.promotions;

import com.notonthehighstreet.concurrent.FunctionalReadWriteLock;
import com.notonthehighstreet.entities.Product;
import com.notonthehighstreet.entities.PromotionDetails;
import com.notonthehighstreet.service.Checkout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;

@ExtendWith(MockitoExtension.class)
public class CheckoutTest {

    private final Product p1 = new Product("test1", "test1", new BigDecimal("1.01"));
    private final Product p2 = new Product("test2", "test2", new BigDecimal("2.00"));

    @Mock
    private FunctionalReadWriteLock lock;
    @Mock
    private Promotion promotion;
    @Mock
    private Promotion promotion2;

    @SuppressWarnings("unchecked")
    @BeforeEach
    public void setup() {
        //setup the lock mock to invoke the supplied supplier/runnable
        willAnswer(invocation -> {
            Supplier<Map<Product, Integer>> argument = invocation.getArgument(0);
            return argument.get();
        }).given(lock).read(any(Supplier.class));
        willAnswer(invocation -> {
            Runnable argument = invocation.getArgument(0);
            argument.run();
            return null;
        }).given(lock).write(any(Runnable.class));
    }

    @Test
    public void givenCheckoutWithMultipleProductsNoPromosShouldCalculateValidTotal() {
        //given
        var checkout = new Checkout(List.of(), lock);
        checkout.scan(p1);
        checkout.scan(p2);
        checkout.scan(p1);

        //when
        var value = checkout.total();

        //then
        var expected = p1.getPrice().multiply(new BigDecimal("2")).add(p2.getPrice()).doubleValue();
        assertEquals(expected, value);
    }

    @Test
    public void givenCheckoutWithMultipleProductsNoMatchingPromosShouldCalculateValidTotal() {
        //given
        var basketValue = p1.getPrice().multiply(new BigDecimal("2")).add(p2.getPrice());
        given(promotion.applyPromo(Map.of(p1, 2, p2, 1), basketValue)).willReturn(Optional.empty());
        var checkout = new Checkout(List.of(promotion), lock);
        checkout.scan(p1);
        checkout.scan(p2);
        checkout.scan(p1);

        //when
        var value = checkout.total();

        //then
        assertEquals(basketValue.doubleValue(), value);
    }

    @Test
    public void givenCheckoutWithMultipleProductsMatchingPromoShouldCalculateValidTotal() {
        //given
        var basketValue = p1.getPrice().multiply(new BigDecimal("2")).add(p2.getPrice());
        var discount = new BigDecimal("1.00");
        given(promotion.applyPromo(Map.of(p1, 2, p2, 1), basketValue))
                .willReturn(Optional.of(new PromotionDetails("testDescription",
                        discount,
                        1)));
        var checkout = new Checkout(List.of(promotion), lock);
        checkout.scan(p1);
        checkout.scan(p2);
        checkout.scan(p1);

        //when
        var value = checkout.total();

        //then
        assertEquals(basketValue.subtract(discount).doubleValue(), value);
    }

    @Test
    public void givenCheckoutWithMultipleProductsMultipleMatchingPromosShouldCalculateValidTotal() {
        //given
        var basketValue = p1.getPrice().multiply(new BigDecimal("2")).add(p2.getPrice());
        var discount1 = new BigDecimal("0.60");
        var discount2 = new BigDecimal("0.804");
        given(promotion.applyPromo(Map.of(p1, 2, p2, 1), basketValue))
                .willReturn(Optional.of(new PromotionDetails("testDescription",
                        discount1,
                        1)));
        given(promotion2.applyPromo(Map.of(p1, 2, p2, 1), basketValue.subtract(discount1)))
                .willReturn(Optional.of(new PromotionDetails("testDescription2",
                        discount2,
                        1)));
        var checkout = new Checkout(List.of(promotion, promotion2), lock);
        checkout.scan(p1);
        checkout.scan(p2);
        checkout.scan(p1);

        //when
        var value = checkout.total();

        //then
        var expected = basketValue.subtract(discount1).subtract(discount2)
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
        assertEquals(expected, value);
    }

}
