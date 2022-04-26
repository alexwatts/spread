package com.alwa.spread;

import java.util.function.Function;

public class ListSpread<T> extends Spread<T> {

    private final int steps;

    public ListSpread(Function<?, ?> stepFunction,
                      Function<?, ?> mapFunction,
                      int steps,
                      Object... seedsOrExamples
                      ) {
        super(stepFunction, mapFunction, seedsOrExamples);
        this.steps = steps;
    }

    public int getSteps() {
        return steps;
    }

}