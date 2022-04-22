package com.alwa.spread;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class ShortFunctionResolver extends StepFunctionResolver {

    @Override
    public Object[] initialiseValuesMap(
        String valuesMapKey,
        int totalSteps,
        Object example,
        RoundingMode roundingMode,
        BigDecimal fractionalAtom) {

            Object[] values = new Object[totalSteps];
            Short seed = (Short) example;
            int baseValue = seed / totalSteps;
            Arrays.fill(values, baseValue);
            int step = baseValue * totalSteps;
            for (int i = 0; i < (short) example - step; i++) {
                values[i] = (int) values[i] + 1;
            }
            valuesMap.put(valuesMapKey, values);
            return valuesMap.get(valuesMapKey);
    }

    @Override
    protected boolean validateSeed(Object seed) {
        return seed != null && (Short) seed > 0;
    }

}