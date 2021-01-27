package com.notonthehighstreet.entities;

import java.math.BigDecimal;
import java.util.Objects;

public class PromotionDetails {

    private final String promoDescription;
    private final BigDecimal discountToApply;
    private final int numberOfTimesApplied;

    public PromotionDetails(String promoDescription,
                            BigDecimal discountToApply,
                            int numberOfTimesApplied) {
        this.promoDescription = promoDescription;
        this.discountToApply = discountToApply;
        this.numberOfTimesApplied = numberOfTimesApplied;
    }

    public String getPromoDescription() {
        return promoDescription;
    }

    public BigDecimal getDiscountToApply() {
        return discountToApply;
    }

    public int getNumberOfTimesApplied() {
        return numberOfTimesApplied;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PromotionDetails that = (PromotionDetails) o;
        return numberOfTimesApplied == that.numberOfTimesApplied &&
                Objects.equals(promoDescription, that.promoDescription) &&
                Objects.equals(discountToApply, that.discountToApply);
    }

    @Override
    public int hashCode() {
        return Objects.hash(promoDescription, discountToApply, numberOfTimesApplied);
    }
}
