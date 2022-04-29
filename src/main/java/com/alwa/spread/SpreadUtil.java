package com.alwa.spread;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.alwa.spread.SpreadValidator.validateCumulativeSpread;

public class SpreadUtil {

    private static <T> Spread<T> spread(T seedOrExamples) {
        return new Spread<>(null, null, seedOrExamples);
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

    private static <T> Spread<T> cumulativeSpread(BigDecimal seed, BigDecimal fractionalAtom) {
        return new CumulativeSpread<>(null, null, RoundingMode.DOWN, fractionalAtom, seed);
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

    public static <T> Spread<List<T>> list(Spread<T> spread, int steps) {
        return new ListSpread<>(null, null, steps, spread);
    }

    public static <T> Spread<List<T>> list(Spreader<T> spreader) {
        return new ListSpread<>(null, null, spreader.getSteps(), spreader);
    }

    public static <T> Spread<Set<T>> set(Spread<T> spread, int steps) {
        return new SetSpread<>(null, null, steps, spread);
    }

    public static <T> Spread<Set<T>> set(Spreader<T> spreader) {
        return new SetSpread<>(null, null, spreader.getSteps(), spreader);
    }

    public static <K, V> Spread<Map<K, V>> map(Spread<K> keySpread, Spread<V> valueSpread, int steps) {
        return new MapSpread<>(null, null, steps, keySpread, valueSpread);
    }

    public static <K, V> Spread<Map<K, V>> map(Spreader<K> keySpread, Spreader<V> valueSpread) {
        return new MapSpread<>(null, null, valueSpread.getSteps(), keySpread, valueSpread);
    }

}
