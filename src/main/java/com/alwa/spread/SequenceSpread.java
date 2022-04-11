package com.alwa.spread;

import java.util.function.Function;

public class SequenceSpread<T> extends Spread<T> {

    public SequenceSpread(Function<?, ?> stepFunction,
                          Function<?, ?> mapFunction,
                          Object... seedsOrExamples) {
        super(stepFunction, mapFunction, seedsOrExamples);
    }

}