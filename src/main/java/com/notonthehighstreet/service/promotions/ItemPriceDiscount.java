package com.notonthehighstreet.service.promotions;

import com.notonthehighstreet.entities.Product;
import com.notonthehighstreet.entities.PromotionDetails;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public class ItemPriceDiscount implements Promotion {

    private final Product toDiscount;
    private final int promoThreshold;
    private final BigDecimal discount;
    private final String description;

    public ItemPriceDiscount(Product toDiscount, int promoThreshold, BigDecimal discount) {
        this.toDiscount = toDiscount;
        this.promoThreshold = promoThreshold;
        this.discount = discount;
        this.description = "£" + discount.toString() + " off " + toDiscount.getName() + "s when you buy "
                + promoThreshold + " or more";
    }

    @Override
    public Optional<PromotionDetails> applyPromo(Map<Product, Integer> basketProducts,
                                                 BigDecimal totalPriceAfterAppliedPromos) {

        var productCount = basketProducts.get(toDiscount);
        if (productCount != null && productCount >= promoThreshold) {
            return Optional.of(new PromotionDetails(description,
                    discount.multiply(new BigDecimal(productCount)),
                    productCount));
        }

        return Optional.empty();
    }
}
