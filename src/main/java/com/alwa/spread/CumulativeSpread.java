package com.alwa.spread;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;

public class CumulativeSpread<T> extends Spread<T> {

    private RoundingMode roundingMode;
    private BigDecimal fractionalAtom;

    public CumulativeSpread(
        Function<?, ?> stepFunction,
        Function<?, ?> mapFunction,
        RoundingMode roundingMode,
        BigDecimal fractionalAtom,
        Object... seedOrExamples) {
        super(stepFunction, mapFunction, seedOrExamples);
        this.roundingMode = roundingMode;
        this.fractionalAtom = fractionalAtom;
    }

    public RoundingMode getRoundingMode() {
        return roundingMode;
    }

    public BigDecimal getFractionalAtom() {
        return fractionalAtom;
    }
}