package com.alwa.spread;

import java.util.function.Function;

public class RelatedSpread<T> extends Spread<T> {

    public RelatedSpread(Function<?, ?> stepFunction,
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

        return stepFunction.apply(((Spread) seedsOrExamples[0]).previousValue(currentStep, ((Spread) seedsOrExamples[0]).values));
    }

}