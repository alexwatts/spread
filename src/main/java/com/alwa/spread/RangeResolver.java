package com.alwa.spread;

import java.math.BigDecimal;
import java.util.function.Function;

public class RangeResolver {

    private final Object example;

    public RangeResolver(Object example) {
        this.example = example;
    }

    public Function resolveStepFunction() {
        return (Function<BigDecimal, BigDecimal>) value -> value.add(BigDecimal.ONE);
    }

}
