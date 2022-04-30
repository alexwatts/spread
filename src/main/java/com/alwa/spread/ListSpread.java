package com.alwa.spread;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListSpread<T> extends Spread<T> {

    private final int steps;

    public ListSpread(
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
            return targetSpread.spread().collect(Collectors.toList());

        } else {

            Spread<T> targetSpread = (Spread<T>) seedsOrExamples[0];

            return new Spreader<List<T>>()
                .factory(ArrayList::new)
                .mutators(list -> list.add(Spread.in(targetSpread)))
                .steps(this.getSteps())
                .spread()
                .collect(Collectors.toList()).get(0);

        }

    }

    @Override
    public <R> Spread<R> step(Function<? super T, ? extends R> stepFunction) {
        this.stepFunction = stepFunction;
        return new ListSpread<>(stepFunction, mapFunction, this.getSteps(), seedsOrExamples);
    }

    @Override
    public <R> Spread<R> map(Function<? super T, ? extends R> mapFunction) {
        this.mapFunction = mapFunction;
        return new ListSpread<>(stepFunction, mapFunction, this.getSteps(), seedsOrExamples);
    }

    public int getSteps() {
        return steps;
    }

}