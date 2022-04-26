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
    protected <R> Spread<R> step(Function<? super T, ? extends R> stepFunction) {
        this.stepFunction = stepFunction;
        return new CumulativeSpread<>(
            stepFunction,
            mapFunction, this.getRoundingMode(),
            this.getFractionalAtom(),
            seedsOrExamples
        );
    }

    @Override
    protected <R> Spread<R> map(Function<? super T, ? extends R> mapFunction) {
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

}