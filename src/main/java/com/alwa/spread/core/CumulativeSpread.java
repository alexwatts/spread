package com.alwa.spread.core;

import com.alwa.spread.RangeResolver;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;

public class CumulativeSpread<T> extends Spread<T> {

    private final RoundingMode roundingMode;
    private final BigDecimal fractionalAtom;

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

    @Override
    protected Object applyStep(int totalSteps,
                               int currentStep,
                               Function<Object, Object> stepFunction,
                               Object[] seedsOrExamples,
                               Object previousValue) {
        RangeResolver rangeResolver = new RangeResolver(seedsOrExamples[0]);
        Function<Object, Object> cumulativeStepFunction =
            rangeResolver.resolveStepFunction(totalSteps, currentStep, this.getRoundingMode(), this.getFractionalAtom());
        return cumulativeStepFunction.apply(seedsOrExamples[0]);
    }

    @Override
    public <R> Spread<R> step(Function<? super T, ? extends R> stepFunction) {
        this.stepFunction = stepFunction;
        return new CumulativeSpread<>(
            stepFunction,
            mapFunction, this.getRoundingMode(),
            this.getFractionalAtom(),
            seedsOrExamples
        );
    }

    @Override
    public <R> Spread<R> map(Function<? super T, ? extends R> mapFunction) {
        this.mapFunction = mapFunction;
        return new CumulativeSpread<>(
            stepFunction,
            mapFunction, this.getRoundingMode(),
            this.getFractionalAtom(),
            seedsOrExamples
        );
    }

    public RoundingMode getRoundingMode() {
        return roundingMode;
    }

    public BigDecimal getFractionalAtom() {
        return fractionalAtom;
    }

    protected CumulativeSpread(CumulativeSpread another)
    {
        super(another);
        this.roundingMode = another.roundingMode;
        this.fractionalAtom = another.fractionalAtom;
    }

    public Object clone()
    {
        return new CumulativeSpread<T>(this);
    }

}