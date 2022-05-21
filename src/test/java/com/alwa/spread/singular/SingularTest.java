package com.alwa.spread.singular;

import com.alwa.spread.*;
import com.alwa.spread.annotations.Embed;
import com.alwa.spread.annotations.In;
import com.alwa.spread.core.Spread;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SingularTest {

    private final Product PRODUCT_ONE = new Product("ALWA1", BigDecimal.valueOf(13.99));
    private final Product PRODUCT_TWO = new Product("ALWA2", BigDecimal.valueOf(5.99));
    private final Product PRODUCT_THREE = new Product("ALWA3", BigDecimal.valueOf(24.99));

    @In
    private final Spread<Product> THREE_PRODUCTS =
        SpreadUtil.sequence(PRODUCT_ONE, PRODUCT_TWO, PRODUCT_THREE);

    @In
    private final Spread<Integer> VARIABLE_QUANTITIES = SpreadUtil.sequence(1, 2, 3);

    @In
    @Embed(clazz = List.class, steps = 3)
    private final Spread<OrderLine> ORDER_LINES =
        SpreadUtil.complexType(
                new Spreader<OrderLine>()
                    .factory(
                        () -> new OrderLine(Spread.in(THREE_PRODUCTS), Spread.in(VARIABLE_QUANTITIES))
                    )
        );

    @In
    Spread<String> CUSTOMER_ID = SpreadUtil.fixed("ALWA123");

    @BeforeEach
    public void setUp() {
        SpreadUtil.initPackage(
            this,
            this.getClass().getPackage().getName()
        );
    }

    @Test
    public void testOrderLinesTotalUpToCorrectPrice() {

        Order ORDER =
            new Spreader<Order>()
                .factory(() -> new Order(Spread.in(CUSTOMER_ID), (List<OrderLine>)Spread.embed(ORDER_LINES)))
                .steps(1)
                .singular();

        assertThat(ORDER.getOrderTotal()).isEqualTo(BigDecimal.valueOf(100.94));
    }



}