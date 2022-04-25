package com.alwa.spread;

import java.util.function.Function;

public class RelatedSpread<T> extends Spread<T> {

    public RelatedSpread(Function<?, ?> stepFunction,
                         Function<?, ?> mapFunction,
                         Object... seedsOrExamples) {
        super(stepFunction, mapFunction, seedsOrExamples);
    }
}