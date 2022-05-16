package com.alwa.spread.core;

import com.alwa.spread.SpreadUtil;
import com.alwa.spread.annotations.Embed;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Spread<T> extends BaseSpread implements Cloneable {

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

    public static <T> T in(Spread<T> of, int steps) {
        if (isSequenceOfSpreads(of)) {
            return nextSequencedValue((SequenceSpread<T>)of, steps);
        }
        return of.next();
    }

    private static <T> T nextSequencedValue(SequenceSpread<T> of, int steps) {
        T nextValue = (T)((Spread)of.values[of.current]).next();
        int sequenceCurrent = ((Spread)of.values[of.current]).current;
        if (sequenceCurrent != 0 && sequenceCurrent % steps == 0) {
            of.next();
        }
        return nextValue;
    }

    private static <T> boolean isSequenceOfSpreads(Spread<T> of) {
        return (of instanceof SequenceSpread) && (of.seedsOrExamples[0] instanceof Spread);
    }

    public static <T> Collection<T> embed(Spread<T> of) {
        return resolveEmbeded(of);
    }

    public static <K, V> Map<K, V> embedMap(Spread<V> of, Spread<K> mapKey) {
        return resolveEmbedded(of, mapKey);
    }

    private static <T> Collection<T> resolveEmbeded(Spread<T> of) {
        Embed embed = SpreadUtil.embedContainers.get(of);
        Collection collectionObject = createContainer(embed);
        for (int i = 0; i < embed.steps(); i++) {
            collectionObject.add(of.next());
        }
        return collectionObject;
    }

    private static <K, V> Map<K, V> resolveEmbedded(Spread<V> of, Spread<K> mapKey) {
        Embed embed = SpreadUtil.embedContainers.get(of);
        Map mapObject = new HashMap<>();
        for (int i = 0; i < embed.steps(); i++) {
            mapObject.put(mapKey.next(), of.next());
        }
        return mapObject;
    }

    private static Collection createContainer(Embed embed) {
        if (embed.clazz().equals(List.class)) {
            return new ArrayList();
        } else if (embed.clazz().equals(Set.class)) {
            return new HashSet();
        } else {
            return new ArrayList();
        }
    }

    public void init(int steps) {
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

    protected void reInitialise(int steps) {
        initialised = false;
        current = null;
        values = null;
        this.init(steps);
    }

    protected T next() {
        wrapValuesOrReInit();
        T value = (T) getValues()[current];
        if (mapFunction != null) {
            value = (T) ((Function<Object, Object>)mapFunction).apply(value);
        }
        current++;
        return value;
    }

    private void wrapValuesOrReInit() {
        if (current == getValues().length) {
            current = 0;
            if (SpreadUtil.dynamicSpreads != null && SpreadUtil.dynamicSpreads.containsKey(this)) {
                this.reInitialise(getValues().length);
            }
        }
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

    protected Spread(Spread another)
    {
        this.mapFunction = another.mapFunction;
        this.stepFunction = another.stepFunction;
        this.seedsOrExamples = another.seedsOrExamples;
    }

    public Object clone()
    {
        return new Spread<T>(this);
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
