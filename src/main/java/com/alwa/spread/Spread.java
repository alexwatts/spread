package com.alwa.spread;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Spread<T> extends BaseSpread {

    private boolean initialised;
    private Integer current;
    protected final Object[] seedsOrExamples;
    protected Function<?, ?> stepFunction;
    protected Function<?, ?> mapFunction;

    public Spread(
            Function<?, ?> stepFunction,
            Function<?, ?> mapFunction,
            Object... seedsOrExamples) {
        this.seedsOrExamples = Arrays.stream(seedsOrExamples).toArray();
        this.stepFunction = stepFunction;
        this.mapFunction = mapFunction;
    }

    public <R> Spread<R> step(Function<? super T, ? extends R> stepFunction) {
        this.stepFunction = stepFunction;
        return new Spread<>(stepFunction, mapFunction, seedsOrExamples);
    }

    public <R> Spread<R> map(Function<? super T, ? extends R> mapFunction) {
        this.mapFunction = mapFunction;
        return new Spread<>(stepFunction, mapFunction, seedsOrExamples);
    }

    public boolean isInitialised() {
        return initialised;
    }

    public static <T> T in(Spread<T> of) {
        return of.next();
    }

    protected void init(int steps) {
        values = new Object[steps];
        IntStream.range(0, steps)
            .forEach(i ->
                values[i] = nextValue(
                    steps,
                    i + 1,
                    ((Function<Object, Object>) stepFunction),
                    seedsOrExamples,
                    previousValue(i, values)
                )
            );
        current = 0;
        initialised = true;
    }

    private T next() {
        T value = (T) getValues()[current];
        if (mapFunction != null) {
            value = (T) ((Function<Object, Object>)mapFunction).apply(value);
        }
        current++;
        return value;
    }

    private Object nextValue(
            int totalSteps,
            int currentStep,
            Function<Object, Object> stepFunction,
            Object[] seedsOrExamples,
            Object previousValue) {
            return applyStep(totalSteps, currentStep, stepFunction, seedsOrExamples, previousValue);
    }

    protected Object applyStep(int totalSteps,
                               int currentStep,
                               Function<Object, Object> stepFunction,
                               Object[] seedsOrExamples,
                               Object previousValue) {

            return stepFunction.apply(previousValue);
    }

    protected Object previousValue(int i, Object[] values) {
        if (i == 0) {
            return seedsOrExamples[0];
        } else {
            return values[i - 1];
        }
    }

    @Override
    public String toString() {
        return String.format("Spread<%s>{" +
            "current=" + current +
            ", seedOrExamples=[" + Arrays.stream(seedsOrExamples).map(Object::toString).collect(Collectors.joining(", ")) + "]" +
            ", stepFunction=" + stepFunction +
            ", mapFunction=" + mapFunction +
            '}', this.getClass());
    }

}
