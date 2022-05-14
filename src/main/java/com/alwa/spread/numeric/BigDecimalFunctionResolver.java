package com.alwa.spread.numeric;

import com.alwa.spread.StepFunctionResolver;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class BigDecimalFunctionResolver extends StepFunctionResolver {

    @Override
    protected boolean validateSeed(Object seed) {
        return seed != null && ((BigDecimal) seed).compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public Object[] initialiseValuesMap(String valuesMapKey, int totalSteps, Object example, RoundingMode roundingMode, BigDecimal fractionalAtom) {
        Object[] values = new Object[totalSteps];
        BigDecimal seed = (BigDecimal) example;
        BigDecimal baseValue;
        BigDecimal fractionalPart = null;

        if (isIntegerValue(seed)) {
            baseValue = seed.divide(BigDecimal.valueOf(totalSteps), roundingMode);
        } else {
            fractionalPart = getFractionalPart(seed);
            baseValue = seed.subtract(fractionalPart)
                    .setScale(0, roundingMode)
                    .divide(BigDecimal.valueOf(totalSteps), roundingMode);
        }

        Arrays.fill(values, baseValue);

        int step = baseValue.intValue() * totalSteps;

        for (int i = 0; i < ((BigDecimal) example).intValue() - step; i++) {
            values[i] = ((BigDecimal) values[i]).add(BigDecimal.ONE);
        }

        int valuesIndex = 0;
        if (fractionalPart != null) {
            while (fractionalPart.compareTo(BigDecimal.ZERO) > 0) {
                if (valuesIndex == values.length) {
                    valuesIndex = 0;
                }
                values[valuesIndex] = ((BigDecimal)values[valuesIndex]).add(fractionalAtom);
                fractionalPart = fractionalPart.subtract(fractionalAtom);
                valuesIndex++;
            }
        }

        valuesMap.put(valuesMapKey, values);
        return valuesMap.get(valuesMapKey);
    }

    private BigDecimal getFractionalPart(BigDecimal seed) {
        return seed.remainder(BigDecimal.ONE);
    }

    private boolean isIntegerValue(BigDecimal bd) {
        return bd.stripTrailingZeros().scale() <= 0;
    }

}
