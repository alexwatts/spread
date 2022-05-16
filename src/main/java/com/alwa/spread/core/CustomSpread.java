package com.alwa.spread.core;


import java.util.function.Function;
import java.util.function.Supplier;

public class CustomSpread<T> extends Spread<T> {

    public CustomSpread(Function<?, ?> stepFunction,
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
        return ((Supplier<T>)seedsOrExamples[0]).get();
    }

    @Override
    public <R> Spread<R> step(Function<? super T, ? extends R> stepFunction) {
        this.stepFunction = stepFunction;
        return new CustomSpread<>(stepFunction, mapFunction, seedsOrExamples);
    }

    @Override
    public <R> Spread<R> map(Function<? super T, ? extends R> mapFunction) {
        this.mapFunction = mapFunction;
        return new CustomSpread<>(stepFunction, mapFunction, seedsOrExamples);
    }

}