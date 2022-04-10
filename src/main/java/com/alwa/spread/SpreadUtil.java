package com.alwa.spread;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.alwa.spread.SpreadValidator.validateCumulativeSpread;

public class SpreadUtil {

    private static <T> Spread<T> spread(T seed) {
        return new Spread<T>(seed, null, null);
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

    public static <T >Spread<T> fixed(T example) {
        return fixedSpread(example);
    }

    private static <T> Spread<T> cumulativeSpread(T seed) {
        return new CumulativeSpread<>(seed, null, null, RoundingMode.DOWN);
    }

    private static <T> Spread<T> cumulativeSpread(BigDecimal seed, RoundingMode roundingMode) {
        return new CumulativeSpread<>(seed, null, null,roundingMode);
    }

    private static <T> Spread<T> fixedSpread(T seed) {
        return new FixedSpread<>(seed);
    }

}
