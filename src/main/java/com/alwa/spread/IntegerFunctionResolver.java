package com.alwa.spread;

import java.math.RoundingMode;
import java.util.Arrays;

public class IntegerFunctionResolver extends StepFunctionResolver {

    @Override
    public Object[] initialiseValuesMap(String valuesMapKey, int totalSteps, Object example, RoundingMode roundingMode) {
        Object[] values = new Object[totalSteps];
        Integer seed = (Integer) example;
        int baseValue = seed / totalSteps;
        Arrays.fill(values, baseValue);
        int step = baseValue * totalSteps;
        for (int i = 0; i < (int) example - step; i++) {
            values[i] = (int) values[i] + 1;
        }
        valuesMap.put(valuesMapKey, values);
        return valuesMap.get(valuesMapKey);
    }

    @Override
    protected boolean validateSeed(Object seed) {
        return seed != null && (Integer) seed > 0;
    }

}