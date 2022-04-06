package com.alwa.spread;

import java.util.function.Function;
import java.util.stream.IntStream;

public class Spread<T> extends BaseSpread {

    private Integer current;

    private final Object seedOrExample;
    private Function<?, ?> stepFunction;
    private Function<?, ?> mapFunction;

    public Spread(Object seedOrExample) {
        this.seedOrExample = seedOrExample;
    }

    public Spread(
            Object seedOrExample,
            Function<?, ?> stepFunction,
            Function<?, ?> mapFunction) {
        this.seedOrExample = seedOrExample;
        this.stepFunction = stepFunction;
        this.mapFunction = mapFunction;
    }

    public <R> Spread<R> step(Function<? super T, ? extends R> stepFunction) {
        this.stepFunction = stepFunction;
        return new Spread<>(seedOrExample, stepFunction, mapFunction);
    }

    public <R> Spread<R> map(Function<? super T, ? extends R> mapFunction) {
        this.mapFunction = mapFunction;
        return new Spread<>(seedOrExample, stepFunction, mapFunction);
    }

    public static <T> T in(Spread<T> of) {
        return of.next();
    }

    protected void init(int steps) {
        values = new Object[steps];
        values[0] = getSeedOrExample();
        IntStream.range(1, steps)
            .forEach(i ->
                values[i] = nextValue(
                    ((Function<Object, Object>) stepFunction),
                    values[i -1]
                )
            );
        current = 0;
    }

    private T next() {
        T value = (T) getValues()[current];
        if (mapFunction != null) {
            value = (T) ((Function<Object, Object>)mapFunction).apply(value);
        }
        current++;
        return value;
    }

    public Object getSeedOrExample() {
        return seedOrExample;
    }

    public Function<?, ?> getStepFunction() {
        return stepFunction;
    }

    public void setStepFunction(Function<?, ?> stepFunction) {
        this.stepFunction = stepFunction;
    }

    public Function<? , ?> getMapFunction() {
        return mapFunction;
    }

    private Object nextValue(Function<Object, Object> stepFunction, Object previousValue) {
        if (stepFunction != null) {
            return stepFunction.apply(previousValue);
        } else {
            return previousValue;
        }
    }

}
