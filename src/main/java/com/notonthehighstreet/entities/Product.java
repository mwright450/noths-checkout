package com.notonthehighstreet.entities;

import java.math.BigDecimal;
import java.util.Objects;

public final class Product {

    private final String productCode;
    private final String name;
    private final BigDecimal price;

    public Product(String productCode, String name, BigDecimal price) {
        this.productCode = productCode;
        this.name = name;
        this.price = price;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(productCode, product.productCode) &&
                Objects.equals(name, product.name) &&
                Objects.equals(price, product.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productCode, name, price);
    }
}
