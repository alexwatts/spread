package com.alwa.spread;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapSpread<K, V> extends Spread<Map<K, V>> {

    private final int steps;

    public MapSpread(
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

        Spread<K> mapKeySpread = (Spread<K>) seedsOrExamples[0];
        Spread<V> mapValueSpread = (Spread<V>) seedsOrExamples[1];
        return new Spreader<Map<K, V>>()
            .factory(HashMap::new)
            .mutators(map -> map.put(Spread.in(mapKeySpread), Spread.in(mapValueSpread)))
            .steps(this.getSteps())
            .debug()
            .spread()
            .collect(Collectors.toList())
            .get(0);

    }

    @Override
    protected <R> Spread<R> step(Function<? super Map<K, V>, ? extends R> stepFunction) {
        this.stepFunction = stepFunction;
        return (Spread<R>) new MapSpread<>(stepFunction, mapFunction, this.getSteps(), seedsOrExamples);
    }

    @Override
    protected <R> Spread<R> map(Function<? super Map<K, V>, ? extends R> mapFunction) {
        this.mapFunction = mapFunction;
        return (Spread<R>) new MapSpread<>(stepFunction, mapFunction, this.getSteps(), seedsOrExamples);
    }

    public int getSteps() {
        return steps;
    }

}