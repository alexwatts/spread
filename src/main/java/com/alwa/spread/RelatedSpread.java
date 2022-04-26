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

    @Override
    protected <R> Spread<R> step(Function<? super T, ? extends R> stepFunction) {
        this.stepFunction = stepFunction;
        return new RelatedSpread<>(stepFunction, mapFunction, seedsOrExamples);
    }

    @Override
    protected <R> Spread<R> map(Function<? super T, ? extends R> mapFunction) {
        this.mapFunction = mapFunction;
        return new RelatedSpread<>(stepFunction, mapFunction, seedsOrExamples);
    }

}