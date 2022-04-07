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
        IntStream.range(0, steps)
            .forEach(i ->
                values[i] = nextValue(
                    steps,
                    i + 1,
                    ((Function<Object, Object>) stepFunction),
                    seedOrExample,
                    previousValue(i, values)
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

    private Object nextValue(
            int totalSteps,
            int currentStep,
            Function<Object, Object> stepFunction,
            Object seedOrExample,
            Object previousValue) {
            return applyCumulativeOrStandardStep(totalSteps, currentStep, stepFunction, seedOrExample, previousValue);
    }

    private Object applyCumulativeOrStandardStep(int totalSteps,
                                                 int currentStep,
                                                 Function<Object, Object> stepFunction,
                                                 Object seedOrExample,
                                                 Object previousValue) {
        if (this instanceof FixedSpread) {
            return seedOrExample;
        }
        else if (this instanceof CumulativeSpread) {
            RangeResolver rangeResolver = new RangeResolver(seedOrExample);
            Function<Object, Object> cumulativeStepFunction =
                    rangeResolver.resolveStepFunction(totalSteps, currentStep, ((CumulativeSpread)this).getRoundingMode());
            return cumulativeStepFunction.apply(seedOrExample);
        } else {
            return stepFunction.apply(previousValue);
        }
    }

    private Object previousValue(int i, Object[] values) {
        if (i == 0) {
            return seedOrExample;
        } else {
            return values[i - 1];
        }
    }

}
