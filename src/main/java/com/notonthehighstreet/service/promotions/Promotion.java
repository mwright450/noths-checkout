package com.notonthehighstreet.service.promotions;

import com.notonthehighstreet.entities.Product;
import com.notonthehighstreet.entities.PromotionDetails;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@FunctionalInterface
public interface Promotion {

    /**
     * Method to apply Promotion implementation to the supplied basket.
     *
     * All products and the current price of the basket after preceding
     * Promotions have been applied should be passed to this method. This allows
     * the specific promotion to be applied depending on a combination of expected
     * products, the total price of the basket at that point in the promo
     * application pipeline or both.
     *
     * @param basketProducts All of the products in the users basket
     * @param totalPriceAfterAppliedPromos The current price of the basket after preceding Promotions have been applied
     * @return
     */
    Optional<PromotionDetails> applyPromo(Map<Product, Integer> basketProducts,
                                          BigDecimal totalPriceAfterAppliedPromos);
}
