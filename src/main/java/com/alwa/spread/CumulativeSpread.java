package com.alwa.spread;

import java.math.RoundingMode;
import java.util.function.Function;

public class CumulativeSpread<T> extends Spread<T> {

    private RoundingMode roundingMode;

    public CumulativeSpread(Object seedOrExample, Function<?, ?> stepFunction, Function<?, ?> mapFunction, RoundingMode roundingMode) {
        super(seedOrExample, stepFunction, mapFunction);
        this.roundingMode = roundingMode;
    }

    public RoundingMode getRoundingMode() {
        return roundingMode;
    }

}
