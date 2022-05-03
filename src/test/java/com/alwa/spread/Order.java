package com.alwa.spread;

import java.math.BigDecimal;
import java.util.List;

public class Order {

    private String customerId;
    private List<OrderLine> orderLines;

    public Order(
        String customerId,
        List<OrderLine> orderLines)  {
        this.customerId = customerId;
        this.orderLines = orderLines;
    }

    public BigDecimal getOrderTotal() {
        return BigDecimal.ZERO;
    }
}
