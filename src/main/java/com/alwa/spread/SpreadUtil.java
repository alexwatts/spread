package com.alwa.spread;

import static com.alwa.spread.SpreadValidator.validateCumulativeObjectType;

public class SpreadUtil {

    private static <T> Spread<T> spread(T seed) {
        return new Spread<T>(seed, null, null);
    }

    public static <T> Spread<T> initial(T seed) {
        return spread(seed);
    }

    public static <T> Spread<T> cumulative(T example) {
        validateCumulativeObjectType(example);
        return cumulativeSpread(example);
    }

    public static <T >Spread<T> fixed(T example) {
        return fixedSpread(example);
    }

    private static <T> Spread<T> cumulativeSpread(T seed) {
        return new CumulativeSpread<>(seed, null, null);
    }

    private static <T> Spread<T> fixedSpread(T seed) {
        return new FixedSpread<>(seed);
    }

}
