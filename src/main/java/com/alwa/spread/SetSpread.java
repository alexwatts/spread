package com.alwa.spread;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SetSpread<T> extends Spread<T> {

    private final int steps;

    public SetSpread(
        Function<?, ?> stepFunction,
        Function<?, ?> mapFunction,
        int steps,
        Object... seedsOrExamples) {
            super(stepFunction, mapFunction, seedsOrExamples);
            this.steps = steps;
    }

    @Override
    protected Object applyStep(int totalSteps,
                               int currentStep,
                               Function<Object, Object> stepFunction,
                               Object[] seedsOrExamples,
                               Object previousValue) {

        if (seedsOrExamples[0] instanceof Spreader) {

            Spreader<T> targetSpread = (Spreader<T>) seedsOrExamples[0];
            return targetSpread.spread().collect(Collectors.toSet());

        } else {

            Spread<T> targetSpread = (Spread<T>) seedsOrExamples[0];

            return new Spreader<Set<T>>()
                .factory(HashSet::new)
                .mutators(set -> set.add(Spread.in(targetSpread)))
                .steps(this.getSteps())
                .spread()
                .collect(Collectors.toSet())
                .stream().findFirst()
                .get();

        }


    }

    @Override
    protected <R> Spread<R> step(Function<? super T, ? extends R> stepFunction) {
        this.stepFunction = stepFunction;
        return new SetSpread<>(stepFunction, mapFunction, this.getSteps(), seedsOrExamples);
    }

    @Override
    protected <R> Spread<R> map(Function<? super T, ? extends R> mapFunction) {
        this.mapFunction = mapFunction;
        return new SetSpread<>(stepFunction, mapFunction, this.getSteps(), seedsOrExamples);
    }

    public int getSteps() {
        return steps;
    }

}