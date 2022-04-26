package com.alwa.spread;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListSpread<T> extends Spread<T> {

    private final int steps;

    public ListSpread(Function<?, ?> stepFunction,
                      Function<?, ?> mapFunction,
                      int steps,
                      Object... seedsOrExamples
                      ) {
        super(stepFunction, mapFunction, seedsOrExamples);
        this.steps = steps;
    }

    @Override
    protected Object applyCumulativeOrStandardStep(int totalSteps,
                                                   int currentStep,
                                                   Function<Object, Object> stepFunction,
                                                   Object[] seedsOrExamples,
                                                   Object previousValue) {

        Spread<T> targetSpread = ((Spread<T>) seedsOrExamples[0]);
        return new Spreader<List<T>>()
            .factory(ArrayList::new)
            .mutators(list -> list.add(Spread.in(targetSpread)))
            .steps(((ListSpread) this).getSteps())
            .debug()
            .spread()
            .collect(Collectors.toList());
    }

    public int getSteps() {
        return steps;
    }

}