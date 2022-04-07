package com.alwa.spread;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;

public class BigDecimalFunctionResolver extends StepFunctionResolver {

    @Override
    Function getStepFunction(int totalSteps, int currentStep, Object seed, RoundingMode roundingMode) {

        Function<BigDecimal, BigDecimal> evenStepFunction =
                previousValue -> ((BigDecimal) seed).divide(BigDecimal.valueOf(totalSteps), roundingMode);

        return evenStepFunction;
    }
}
