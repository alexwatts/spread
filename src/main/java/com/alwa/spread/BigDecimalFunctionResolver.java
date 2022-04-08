package com.alwa.spread;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class BigDecimalFunctionResolver extends StepFunctionResolver {

    @Override
    public Object[] initialiseValuesMap(String valuesMapKey, int totalSteps, Object example, RoundingMode roundingMode) {
        Object[] values = new Object[totalSteps];
        BigDecimal seed = (BigDecimal) example;
        BigDecimal baseValue;
        BigDecimal fractionalPart;
        if (isIntegerValue(seed)) {
            baseValue = seed.divide(BigDecimal.valueOf(totalSteps), roundingMode);
        } else {
            fractionalPart = getFractionalPart(seed);
            baseValue = seed.subtract(fractionalPart);
        }

        Arrays.fill(values, baseValue);
        int step = baseValue.intValue() * totalSteps;
        for (int i = 0; i < ((BigDecimal) example).intValue() - step; i++) {
            values[i] = (int) values[i] + 1;
        }
        valuesMap.put(valuesMapKey, values);
        return valuesMap.get(valuesMapKey);
    }

    private BigDecimal getFractionalPart(BigDecimal seed) {
        return seed.subtract(seed.stripTrailingZeros());
    }

    private boolean isIntegerValue(BigDecimal bd) {
        return bd.stripTrailingZeros().scale() <= 0;
    }
}
