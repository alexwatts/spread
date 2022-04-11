package com.alwa.spread;

import java.util.function.Function;

public class CallSpread<T> extends Spread<T> {

    public CallSpread(Function<?, ?> stepFunction,
                      Function<?, ?> mapFunction,
                      Object... seedsOrExamples) {
        super(stepFunction, mapFunction, seedsOrExamples);
    }

}