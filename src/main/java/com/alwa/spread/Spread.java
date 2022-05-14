package com.alwa.spread;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Spread<T> extends BaseSpread {

    protected boolean initialised;
    protected boolean initialising;
    protected Integer current;
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

    public static <T> Collection<T> embed(Spread<T> of) {
        return resolveEmbeded(of);
    }

    public static <K, V> Map<K, V> embedMap(Spread<V> of, Spread<K> mapKey) {
        return resolveEmbedded(of, mapKey);
    }

    private static <T> Collection<T> resolveEmbeded(Spread<T> of) {
        EmbeddedCollection embeddedCollection = SpreadUtil.embedContainers.get(of);
        Collection collectionObject = ((Collection)embeddedCollection.getContainer());
        collectionObject.clear();
        for (int i = 0; i < embeddedCollection.getSteps(); i++) {
            collectionObject.add(of.next());
        }
        return collectionObject;
    }

    private static <K, V> Map<K, V> resolveEmbedded(Spread<V> of, Spread<K> mapKey) {
        EmbeddedCollection embeddedCollection = SpreadUtil.embedContainers.get(of);
        Map mapObject = ((Map)embeddedCollection.getContainer());
        mapObject.clear();
        for (int i = 0; i < embeddedCollection.getSteps(); i++) {
            mapObject.put(mapKey.next(), of.next());
        }
        return mapObject;
    }

    protected void init(int steps) {
        if (initialising) return;
        initialising = true;
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
        initialising = false;
    }

    private T next() {
        wrapValues();
        T value = (T) getValues()[current];
        if (mapFunction != null) {
            value = (T) ((Function<Object, Object>)mapFunction).apply(value);
        }
        current++;
        return value;
    }

    private void wrapValues() {
        if (current == getValues().length) current = 0;
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
