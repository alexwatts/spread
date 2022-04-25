package com.alwa.spread;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Spread<T> extends BaseSpread {

    private boolean initialised;

    private Integer current;

    private final Object[] seedsOrExamples;
    private Function<?, ?> stepFunction;
    private Function<?, ?> mapFunction;

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
        return newTypedSpread(this.mapFunction, stepFunction);
    }

    public <R> Spread<R> map(Function<? super T, ? extends R> mapFunction) {
        this.mapFunction = mapFunction;
        return newTypedSpread(mapFunction, this.stepFunction);
    }

    public boolean isInitialised() {
        return initialised;
    }

    private <R> Spread<R> newTypedSpread(
        Function<?, ?> mapFunction,
        Function<?, ?> stepFunction) {
        if (this instanceof FixedSpread) {
            return new FixedSpread<>(stepFunction, mapFunction, seedsOrExamples);
        } else if (this instanceof RelatedSpread) {
            return new RelatedSpread<>(stepFunction, mapFunction, seedsOrExamples);
        } else if (this instanceof CumulativeSpread) {
            return new CumulativeSpread<>(
                stepFunction,
                mapFunction, ((CumulativeSpread<T>) this).getRoundingMode(),
                ((CumulativeSpread<T>) this).getFractionalAtom(),
                seedsOrExamples
            );
        } else if (this instanceof SequenceSpread) {
            return new SequenceSpread<>(stepFunction, mapFunction, seedsOrExamples);
        } else if (this instanceof CallSpread) {
            return new CallSpread<>(stepFunction, mapFunction, seedsOrExamples);
        } else {
            return new Spread<>(stepFunction, mapFunction, seedsOrExamples);
        }
    }

    public static <T> T in(Spread<T> of) {
        return of.next();
    }

    protected void init(int steps) {
        if (this instanceof RelatedSpread) {
            if (!((Spread)this.seedsOrExamples[0]).isInitialised()) {
                ((Spread)this.seedsOrExamples[0]).init(steps);
            }
        }
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

    public Object[] getSeedOrExamples() {
        return seedsOrExamples;
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
            Object[] seedsOrExamples,
            Object previousValue) {
            return applyCumulativeOrStandardStep(totalSteps, currentStep, stepFunction, seedsOrExamples, previousValue);
    }

    private Object applyCumulativeOrStandardStep(int totalSteps,
                                                 int currentStep,
                                                 Function<Object, Object> stepFunction,
                                                 Object[] seedsOrExamples,
                                                 Object previousValue) {
        if (this instanceof FixedSpread) {
            return seedsOrExamples[0];
        } else if (this instanceof SequenceSpread) {
            return seedsOrExamples[(currentStep - 1) % seedsOrExamples.length];
        }
        else if (this instanceof CumulativeSpread) {
            RangeResolver rangeResolver = new RangeResolver(seedsOrExamples[0]);
            Function<Object, Object> cumulativeStepFunction =
                rangeResolver.resolveStepFunction(totalSteps, currentStep, ((CumulativeSpread) this).getRoundingMode(), ((CumulativeSpread) this).getFractionalAtom());
            return cumulativeStepFunction.apply(seedsOrExamples[0]);
        } else if (this instanceof CallSpread) {
            return stepFunction.apply(seedsOrExamples[0]);
        } else if (this instanceof RelatedSpread) {
            return stepFunction.apply(((Spread)this.seedsOrExamples[0]).previousValue(currentStep , ((Spread)this.seedsOrExamples[0]).values));
        } else {
            return stepFunction.apply(previousValue);
        }
    }

    private Object previousValue(int i, Object[] values) {
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
            ", seedOrExample=" + seedsOrExamples[0] +
            ", stepFunction=" + stepFunction +
            ", mapFunction=" + mapFunction +
            '}', this.getClass());
    }

}
