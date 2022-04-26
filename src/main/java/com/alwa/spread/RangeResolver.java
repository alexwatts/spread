package com.alwa.spread;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;

public class RangeResolver {

    private final Object example;

    public RangeResolver(Object example) {
        this.example = example;
    }

    public Function<Object, Object> resolveStepFunction(int totalSteps, int currentStep, RoundingMode roundingMode, BigDecimal fractionalAtom) {
        return SupportedFunctionResolvers.supportedFunctionResolvers().get(example.getClass()).getStepFunction(totalSteps, currentStep, example, roundingMode, fractionalAtom);
    }

    public boolean validateSeed() {
        return SupportedFunctionResolvers.supportedFunctionResolvers().get(example.getClass()).validateSeed(example);
    }

}