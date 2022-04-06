package com.alwa.spread;

import java.util.function.Function;

public class CumulativeSpread<T> extends Spread<T> {

    public CumulativeSpread(Object seedOrExample, Function<?, ?> stepFunction, Function<?, ?> mapFunction) {
        super(seedOrExample, stepFunction, mapFunction);
        resolveInitialAndStepFunction();
    }

    private void resolveInitialAndStepFunction() {
        RangeResolver rangeResolver = new RangeResolver(getSeedOrExample());
        setStepFunction(rangeResolver.resolveStepFunction());
    }
}
