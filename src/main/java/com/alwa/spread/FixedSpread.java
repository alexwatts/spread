package com.alwa.spread;

import java.util.function.Function;

public class FixedSpread<T> extends Spread<T> {

    public FixedSpread(Function<?, ?> stepFunction,
                       Function<?, ?> mapFunction,
                       Object... seedsOrExamples) {
        super(stepFunction, mapFunction, seedsOrExamples);
    }

    @Override
    public Object applyCumulativeOrStandardStep(int totalSteps,
                                                   int currentStep,
                                                   Function<Object, Object> stepFunction,
                                                   Object[] seedsOrExamples,
                                                   Object previousValue) {
        return seedsOrExamples[0];
    }

}