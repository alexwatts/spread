package com.alwa.spread;

import java.util.function.Function;

public class CallSpread<T> extends Spread<T> {

    public CallSpread(Function<?, ?> stepFunction,
                      Function<?, ?> mapFunction,
                      Object... seedsOrExamples) {
        super(stepFunction, mapFunction, seedsOrExamples);
    }

    @Override
    protected Object applyCumulativeOrStandardStep(int totalSteps,
                                                   int currentStep,
                                                   Function<Object, Object> stepFunction,
                                                   Object[] seedsOrExamples,
                                                   Object previousValue) {
        return stepFunction.apply(seedsOrExamples[0]);
    }

}