package com.alwa.spread.singular;

import java.math.BigDecimal;

public class Product {

    private final String productCode;
    private final BigDecimal price;

    public Product(String productCode, BigDecimal price) {
        this.productCode = productCode;
        this.price = price;
    }

    public String getProductCode() {
        return productCode;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
