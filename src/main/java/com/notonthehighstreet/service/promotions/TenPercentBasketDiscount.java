package com.notonthehighstreet.service.promotions;

import com.notonthehighstreet.entities.Product;
import com.notonthehighstreet.entities.PromotionDetails;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public class TenPercentBasketDiscount implements Promotion {

    private static final BigDecimal POINT_ONE = new BigDecimal("0.1");

    private final BigDecimal promoThreshold;
    private final String description;

    public TenPercentBasketDiscount(BigDecimal promoThreshold) {
        this.promoThreshold = promoThreshold;
        this.description = "10% discount on carts over £" + promoThreshold.toString();
    }

    @Override
    public Optional<PromotionDetails> applyPromo(Map<Product, Integer> basketProducts,
                                                 BigDecimal totalPriceAfterAppliedPromos) {

        if (totalPriceAfterAppliedPromos.compareTo(promoThreshold) > 0) {
            return Optional.of(new PromotionDetails(description,
                    totalPriceAfterAppliedPromos.multiply(POINT_ONE),
                    1));
        }

        return Optional.empty();
    }
}
