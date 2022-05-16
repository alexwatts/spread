package com.alwa.spread.core;

import com.alwa.spread.Spreader;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ComplexSpread<T> extends Spread<T> {

    public ComplexSpread(Function<?, ?> stepFunction,
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
        return values[currentStep];
    }

    @Override
    public void init(int steps) {
        if (initialising) return;
        initialising = true;
        if (initialised) {
            if (steps < values.length) {
                initialising = false;
                return;
            }
        }
        values = new Object[steps];

        List<T> dataObjects =
            (List<T>)((Spreader)seedsOrExamples[0])
                .steps(steps)
                .spread()
                .collect(Collectors.toList());
        for (int i = 0; i < steps; i++) {
            values[i] = dataObjects.get(i);
        }
        current = 0;
        initialised = true;
        initialising = false;
    }

    @Override
    public <R> Spread<R> step(Function<? super T, ? extends R> stepFunction) {
        this.stepFunction = stepFunction;
        return new ComplexSpread<>(stepFunction, mapFunction, seedsOrExamples);
    }

    @Override
    public <R> Spread<R> map(Function<? super T, ? extends R> mapFunction) {
        this.mapFunction = mapFunction;
        return new ComplexSpread<>(stepFunction, mapFunction, seedsOrExamples);
    }

}