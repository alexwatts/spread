package com.alwa.spread.numeric;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class DoubleFunctionResolver extends StepFunctionResolver {

    @Override
    public boolean validateSeed(Object seed) {
        return seed != null && ((Double) seed).compareTo(0d) > 0;
    }

    @Override
    public Object[] initialiseValuesMap(String valuesMapKey, int totalSteps, Object example, RoundingMode roundingMode, BigDecimal fractionalAtom) {
        Object[] values = new Object[totalSteps];
        Double seed = (Double) example;
        Double baseValue;
        BigDecimal fractionalPart = null;

        if (isIntegerValue(seed)) {
            baseValue = BigDecimal.valueOf(seed).divide(BigDecimal.valueOf(totalSteps), roundingMode).doubleValue();
        } else {
            fractionalPart = getFractionalPart(seed);
            baseValue = BigDecimal.valueOf(seed).subtract(fractionalPart)
                    .setScale(0, roundingMode)
                    .divide(BigDecimal.valueOf(totalSteps), roundingMode).doubleValue();
        }

        Arrays.fill(values, baseValue);

        int step = baseValue.intValue() * totalSteps;

        for (int i = 0; i < ((Double) example).intValue() - step; i++) {
            values[i] = BigDecimal.valueOf((Double) values[i]).add(BigDecimal.ONE).doubleValue();
        }

        int valuesIndex = 0;
        if (fractionalPart != null) {
            while (fractionalPart.compareTo(BigDecimal.ZERO) > 0) {
                if (valuesIndex == values.length) {
                    valuesIndex = 0;
                }
                values[valuesIndex] = BigDecimal.valueOf((Double)values[valuesIndex]).add(fractionalAtom).doubleValue();
                fractionalPart = fractionalPart.subtract(fractionalAtom);
                valuesIndex++;
            }
        }

        valuesMap.put(valuesMapKey, values);
        return valuesMap.get(valuesMapKey);
    }

    private BigDecimal getFractionalPart(Double seed) {
        return BigDecimal.valueOf(seed).remainder(BigDecimal.ONE);
    }

    private boolean isIntegerValue(Double value) {
        return BigDecimal.valueOf(value).stripTrailingZeros().scale() <= 0;
    }

}
