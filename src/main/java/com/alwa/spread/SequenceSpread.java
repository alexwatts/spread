package com.alwa.spread;

import java.util.function.Function;

public class SequenceSpread<T> extends Spread<T> {

    public SequenceSpread(Function<?, ?> stepFunction,
                          Function<?, ?> mapFunction,
                          Object... seedsOrExamples) {
        super(stepFunction, mapFunction, seedsOrExamples);
    }

    @Override
    public Object applyStep(int totalSteps,
                            int currentStep,
                            Function<Object, Object> stepFunction,
                            Object[] seedsOrExamples,
                            Object previousValue) {

        if (seedsOrExamples[0] instanceof Spread) {
            return ((Spread)seedsOrExamples[(currentStep - 1) % seedsOrExamples.length])
                .applyStep(totalSteps, currentStep, stepFunction, ((Spread<?>) seedsOrExamples[(currentStep - 1) % seedsOrExamples.length]).seedsOrExamples, previousValue);
        } else {
            return seedsOrExamples[(currentStep - 1) % seedsOrExamples.length];
        }

    }

    @Override
    public <R> Spread<R> step(Function<? super T, ? extends R> stepFunction) {
        this.stepFunction = stepFunction;
        return new SequenceSpread<>(stepFunction, mapFunction, seedsOrExamples);
    }

    @Override
    public <R> Spread<R> map(Function<? super T, ? extends R> mapFunction) {
        this.mapFunction = mapFunction;
        return new SequenceSpread<>(stepFunction, mapFunction, seedsOrExamples);
    }

}