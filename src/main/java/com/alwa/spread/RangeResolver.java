package com.alwa.spread;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class RangeResolver {

    private final Object example;

    public RangeResolver(Object example) {
        this.example = example;
    }

    public Function resolveStepFunction(int totalSteps, int currentStep, RoundingMode roundingMode, BigDecimal fractionalAtom) {
        return getResolverMap().get(example.getClass()).getStepFunction(totalSteps, currentStep, example, roundingMode, fractionalAtom);
    }

    public boolean validateSeed() {
        return getResolverMap().get(example.getClass()).validateSeed(example);
    }

    private Map<Class, StepFunctionResolver> getResolverMap() {
        Map<Class, StepFunctionResolver> resolverMap = new HashMap<>();
        resolverMap.put(BigDecimal.class, new BigDecimalFunctionResolver());
        resolverMap.put(BigInteger.class, new BigIntegerFunctionResolver());
        resolverMap.put(Double.class, new DoubleFunctionResolver());
        resolverMap.put(Long.class, new LongFunctionResolver());
        resolverMap.put(Integer.class, new IntegerFunctionResolver());
        return resolverMap;
    }

}