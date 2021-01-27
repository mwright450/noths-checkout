package com.notonthehighstreet.service;

import com.notonthehighstreet.concurrent.FunctionalReadWriteLock;
import com.notonthehighstreet.entities.Product;
import com.notonthehighstreet.service.promotions.Promotion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Checkout {

    private static final Logger LOG = LoggerFactory.getLogger(Checkout.class);
    private static final BigDecimal ZERO = new BigDecimal("0.00");

    private final List<Promotion> promotions;
    private final FunctionalReadWriteLock lock;
    private final Map<Product, Integer> products;

    /**
     * List chosen for promotions as application order is important
     * to correctly price the basket.
     *
     * @param promotions The Promotions to apply in order
     */
    public Checkout(List<Promotion> promotions) {
        this(promotions, new FunctionalReadWriteLock());
    }

    public Checkout(List<Promotion> promotions, FunctionalReadWriteLock lock) {
        this.promotions = promotions;
        this.lock = lock;
        this.products = new HashMap<>();
    }

    public void scan(Product product) {
        LOG.info("Adding {} to cart...", product.getName());
        // take a write lock so the products map can be safely mutated
        lock.write(() -> {
            var count = products.get(product);
            if (count == null) {
                count = 1;
            } else {
                count = count + 1;
            }
            LOG.info("Total of {} {} in cart", count, product.getName());
            products.put(product, count);
        });
    }

    /*
     This method returns a Double as this is the interface suggested
     in the test document, this should really be a BigDeciaml as we
     will lose precision in the calculated value using a Double.
     */
    public Double total() {
        // make immutable copy of the products to prevent multi-threading issues
        var productsCopy = lock.read(() -> Map.copyOf(products));
        var totalCartPrice = priceProducts(productsCopy);
        var totalCartPriceAfterPromos = applyPromos(productsCopy, totalCartPrice);

        LOG.info("Total cart price {} total price after promos {}", totalCartPrice,
                totalCartPriceAfterPromos);

        return totalCartPriceAfterPromos.doubleValue();
    }

    private BigDecimal priceProducts(Map<Product, Integer> productsToPrice) {
        BigDecimal totalCartPrice = ZERO;
        for (Map.Entry<Product, Integer> entry : productsToPrice.entrySet()) {
            var product = entry.getKey();
            var quantity = entry.getValue();
            LOG.info("Pricing {} {}", quantity, product.getName());
            var itemPrice = product.getPrice().multiply(new BigDecimal(quantity));
            LOG.info("Pricing before promotions {}", itemPrice);
            totalCartPrice = totalCartPrice.add(itemPrice);
        }
        LOG.info("Total cart price before promotions {}", totalCartPrice);
        return totalCartPrice;
    }

    private BigDecimal applyPromos(Map<Product, Integer> productsCopy, BigDecimal totalCartPrice) {
        var totalCartPriceAfterPromos = totalCartPrice;
        for (Promotion promo : promotions) {
            var promotionDetailsOpt = promo.applyPromo(productsCopy, totalCartPriceAfterPromos);
            if (promotionDetailsOpt.isPresent()) {
                var promotionDetails = promotionDetailsOpt.get();
                LOG.info("Applying {}", promotionDetails.getPromoDescription());
                totalCartPriceAfterPromos = totalCartPriceAfterPromos
                        .subtract(promotionDetails.getDiscountToApply());
            }
        }
        // round the calculated price after promotions to 2dp
        return totalCartPriceAfterPromos.setScale(2, RoundingMode.HALF_UP);
    }
}
