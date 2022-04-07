package com.alwa.spread;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class IntegerFunctionResolver extends StepFunctionResolver {

    Map<String, Object[]> valuesMap = new HashMap<>();

    @Override
    Function<Object, Object> getStepFunction(int totalSteps, int currentStep, Object example, RoundingMode roundingMode) {
        return  value -> getValue(totalSteps, currentStep, example, roundingMode);
    }

    private Object getValue(int totalSteps, int currentStep, Object example, RoundingMode roundingMode) {
        String valuesMapKey = valuesMapKey(totalSteps, example);
        if (valuesMap.containsKey(valuesMapKey)) {
            return valuesMap.get(valuesMapKey)[currentStep];
        } else {
            return initialiseValuesMap(valuesMapKey, totalSteps, example)[currentStep - 1];
        }
    }

    private Object[] initialiseValuesMap(String valuesMapKey, int totalSteps, Object example) {
        Object[] values = new Object[totalSteps];
        Integer seed = (Integer)example;
        int baseValue = seed / totalSteps;
        for (int i = 0; i < values.length; i++) {
            values[i] = baseValue;
        }
        int step = baseValue * totalSteps;
        for (int i = 0; i < (int)example - step; i++) {
            values[i] = (int)values[i] + 1;
        }
        valuesMap.put(valuesMapKey, values);
        return valuesMap.get(valuesMapKey);
    }

    private String valuesMapKey(int totalSteps, Object example) {
        return Integer.valueOf(totalSteps).toString() + "-" + example.toString();
    }

}