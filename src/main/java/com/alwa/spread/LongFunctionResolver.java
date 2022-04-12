package com.alwa.spread;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class LongFunctionResolver extends StepFunctionResolver {

    @Override
    public Object[] initialiseValuesMap(
        String valuesMapKey,
        int totalSteps,
        Object example,
        RoundingMode roundingMode,
        BigDecimal fractionalAtom) {

            Object[] values = new Object[totalSteps];
            Long seed = (Long) example;
            int baseValue = seed.intValue() / totalSteps;
            Arrays.fill(values, (long) baseValue);
            int step = baseValue * totalSteps;
            for (int i = 0; i < (Long) example - step; i++) {
                values[i] = (Long) values[i] + 1L;
            }
            valuesMap.put(valuesMapKey, values);
            return valuesMap.get(valuesMapKey);
    }

    @Override
    protected boolean validateSeed(Object seed) {
        return seed != null && (Long) seed > 0;
    }

}