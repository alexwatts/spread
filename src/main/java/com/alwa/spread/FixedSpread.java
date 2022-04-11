package com.alwa.spread;

import java.util.function.Function;

public class FixedSpread<T> extends Spread<T> {

    public FixedSpread(Function<?, ?> stepFunction,
                       Function<?, ?> mapFunction,
                       Object... seedsOrExamples) {
        super(stepFunction, mapFunction, seedsOrExamples);
    }

}