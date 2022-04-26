package com.alwa.spread;

import java.util.function.Function;

public class CallSpread<T> extends Spread<T> {

    public CallSpread(Function<?, ?> stepFunction,
                      Function<?, ?> mapFunction,
                      Object... seedsOrExamples) {
        super(stepFunction, mapFunction, seedsOrExamples);
    }

    @Override
    protected Object applyStep(int totalSteps,
                               int currentStep,
                               Function<Object, Object> stepFunction,
                               Object[] seedsOrExamples,
                               Object previousValue) {
        return stepFunction.apply(seedsOrExamples[0]);
    }

    @Override
    protected <R> Spread<R> step(Function<? super T, ? extends R> stepFunction) {
        this.stepFunction = stepFunction;
        return new CallSpread<>(stepFunction, mapFunction, seedsOrExamples);
    }

    @Override
    protected <R> Spread<R> map(Function<? super T, ? extends R> mapFunction) {
        this.mapFunction = mapFunction;
        return new CallSpread<>(stepFunction, mapFunction, seedsOrExamples);
    }

}