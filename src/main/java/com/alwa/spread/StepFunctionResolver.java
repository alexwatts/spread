package com.alwa.spread;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class StepFunctionResolver {

    protected abstract Object[] initialiseValuesMap(String valuesMapKey, int totalSteps, Object seed, RoundingMode roundingMode);

    protected abstract boolean validateSeed(Object seed);

    Map<String, Object[]> valuesMap = new HashMap<>();


    Function<Object, Object> getStepFunction(int totalSteps, int currentStep, Object seed, RoundingMode roundingMode) {
        return  value -> getValue(totalSteps, currentStep, seed, roundingMode);
    }

    private String valuesMapKey(int totalSteps, Object example) {
        return Integer.valueOf(totalSteps).toString() + "-" + example.toString();
    }

    private Object getValue(int totalSteps, int currentStep, Object seed, RoundingMode roundingMode) {
        String valuesMapKey = valuesMapKey(totalSteps, seed);
        if (valuesMap.containsKey(valuesMapKey)) {
            return valuesMap.get(valuesMapKey)[currentStep - 1];
        } else {
            return initialiseValuesMap(valuesMapKey, totalSteps, seed, roundingMode)[currentStep - 1];
        }
    }

}