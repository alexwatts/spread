package com.alwa.spread;

import java.math.BigDecimal;

public class Product {

    private String productCode;
    private BigDecimal price;

    public Product(String productCode, BigDecimal price) {
        this.productCode = productCode;
        this.price = price;
    }
}
