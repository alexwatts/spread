package com.alwa.spread;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;

import static com.alwa.spread.SpreadValidator.validateCumulativeSpread;

public class SpreadUtil {

    private static <T> Spread<T> spread(T seedOrExamples) {
        return new Spread<T>(null, null, seedOrExamples);
    }

    public static <T> Spread<T> initial(T seed) {
        return spread(seed);
    }

    public static <T> Spread<T> cumulative(BigDecimal example, RoundingMode roundingMode) {
       return cumulativeSpread(example, roundingMode);
    }

    public static <T> Spread<T> cumulative(T example) {
        validateCumulativeSpread(example);
        return cumulativeSpread(example);
    }

    public static <T>Spread<T> fixed(T example) {
        return fixedSpread(example);
    }

    private static <T> Spread<T> cumulativeSpread(T seed) {
        return new CumulativeSpread<>(null, null, RoundingMode.DOWN, BigDecimal.valueOf(0.01), seed);
    }

    private static <T> Spread<T> cumulativeSpread(T seed, BigDecimal fractionalAtom) {
        return new CumulativeSpread<>(null, null, RoundingMode.DOWN, fractionalAtom, seed);
    }

    private static <T> Spread<T> cumulativeSpread(BigDecimal seed, RoundingMode roundingMode) {
        return new CumulativeSpread<>(null, null, roundingMode, BigDecimal.valueOf(0.01), seed);
    }

    private static <T> Spread<T> cumulativeSpread(BigDecimal seed, RoundingMode roundingMode, BigDecimal fractionalAtom) {
        return new CumulativeSpread<>(null, null, roundingMode, fractionalAtom, seed);
    }

    private static <T> Spread<T> fixedSpread(T seed) {
        return new FixedSpread<>(null, null, seed);
    }

    public static <T> Spread<T> sequence(T... examples) {
        return new SequenceSpread<>(null, null, examples);
    }

    public static Spread<String> custom(Function<?, ?> functionToCall) {
        return new CallSpread<>(functionToCall, null, new Object());
    }

    public static  <T> Spread<T> related(Spread<T> related) {
        return new RelatedSpread<>(null, null, related);
    }
}
