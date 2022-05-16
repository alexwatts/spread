package com.alwa.spread.numeric;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class StepFunctionResolver {

    protected abstract Object[] initialiseValuesMap(String valuesMapKey, int totalSteps, Object seed, RoundingMode roundingMode, BigDecimal fractionalAtom);

    public abstract boolean validateSeed(Object seed);

    protected Map<String, Object[]> valuesMap = new HashMap<>();

    public Function<Object, Object> getStepFunction(int totalSteps, int currentStep, Object seed, RoundingMode roundingMode, BigDecimal fractionalAtom) {
        return  value -> getValue(totalSteps, currentStep, seed, roundingMode, fractionalAtom);
    }

    private String valuesMapKey(int totalSteps, Object example) {
        return Integer.valueOf(totalSteps).toString() + "-" + example.toString();
    }

    private Object getValue(int totalSteps, int currentStep, Object seed, RoundingMode roundingMode, BigDecimal fractionalAtom) {
        String valuesMapKey = valuesMapKey(totalSteps, seed);
        if (valuesMap.containsKey(valuesMapKey)) {
            return valuesMap.get(valuesMapKey)[currentStep - 1];
        } else {
            return initialiseValuesMap(valuesMapKey, totalSteps, seed, roundingMode, fractionalAtom)[currentStep - 1];
        }
    }

}