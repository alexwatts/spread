package com.alwa.spread.singular;

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
        return orderLines
            .stream()
            .map(orderLine -> orderLine.getProduct().getPrice().multiply(BigDecimal.valueOf(orderLine.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
