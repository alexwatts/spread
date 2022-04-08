package com.alwa.spread;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class StepFunctionResolver {

    Map<String, Object[]> valuesMap = new HashMap<>();

    protected abstract Object[] initialiseValuesMap(String valuesMapKey, int totalSteps, Object example, RoundingMode roundingMode);

    Function<Object, Object> getStepFunction(int totalSteps, int currentStep, Object example, RoundingMode roundingMode) {
        return  value -> getValue(totalSteps, currentStep, example, roundingMode);
    }

    private String valuesMapKey(int totalSteps, Object example) {
        return Integer.valueOf(totalSteps).toString() + "-" + example.toString();
    }

    private Object getValue(int totalSteps, int currentStep, Object example, RoundingMode roundingMode) {
        String valuesMapKey = valuesMapKey(totalSteps, example);
        if (valuesMap.containsKey(valuesMapKey)) {
            return valuesMap.get(valuesMapKey)[currentStep - 1];
        } else {
            return initialiseValuesMap(valuesMapKey, totalSteps, example, roundingMode)[currentStep - 1];
        }
    }

}