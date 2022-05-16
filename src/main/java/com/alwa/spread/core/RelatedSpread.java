package com.alwa.spread.core;

import java.util.function.Function;

public class RelatedSpread<T> extends Spread<T> {

    public RelatedSpread(Function<?, ?> stepFunction,
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

        return stepFunction
            .apply(((Spread<?>) seedsOrExamples[0])
            .previousValue(currentStep, ((Spread<?>) seedsOrExamples[0]).values));
    }

    @Override
    public void init(int steps) {
        if (!((Spread<?>)this.seedsOrExamples[0]).isInitialised()) {
            ((Spread<?>)this.seedsOrExamples[0]).init(steps);
        }
        super.init(steps);
    }

    @Override
    public <R> Spread<R> step(Function<? super T, ? extends R> stepFunction) {
        this.stepFunction = stepFunction;
        return new RelatedSpread<>(stepFunction, mapFunction, seedsOrExamples);
    }

    @Override
    public <R> Spread<R> map(Function<? super T, ? extends R> mapFunction) {
        this.mapFunction = mapFunction;
        return new RelatedSpread<>(stepFunction, mapFunction, seedsOrExamples);
    }

    protected RelatedSpread(RelatedSpread another)
    {
        super(another);
    }

    public Object clone()
    {
        return new RelatedSpread<T>(this);
    }

}