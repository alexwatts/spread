package com.alwa.spread.numeric;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class FloatFunctionResolver extends StepFunctionResolver {

    @Override
    public boolean validateSeed(Object seed) {
        return seed != null && ((Double) seed).compareTo(0d) > 0;
    }

    @Override
    public Object[] initialiseValuesMap(String valuesMapKey, int totalSteps, Object example, RoundingMode roundingMode, BigDecimal fractionalAtom) {
        Object[] values = new Object[totalSteps];
        Float seed = (Float) example;
        Float baseValue;
        BigDecimal fractionalPart = null;

        if (isIntegerValue(seed)) {
            baseValue = BigDecimal.valueOf(seed.doubleValue()).divide(BigDecimal.valueOf(totalSteps), roundingMode).floatValue();
        } else {
            fractionalPart = getFractionalPart(seed.doubleValue());
            baseValue = BigDecimal.valueOf(seed.doubleValue()).subtract(fractionalPart)
                    .setScale(0, roundingMode)
                    .divide(BigDecimal.valueOf(totalSteps), roundingMode).floatValue();
        }

        Arrays.fill(values, baseValue);

        int step = baseValue.intValue() * totalSteps;

        for (int i = 0; i < ((Float) example).intValue() - step; i++) {
            values[i] = BigDecimal.valueOf((Float) values[i]).add(BigDecimal.ONE).floatValue();
        }

        int valuesIndex = 0;
        if (fractionalPart != null) {
            while (fractionalPart.compareTo(BigDecimal.ZERO) > 0) {
                if (valuesIndex == values.length) {
                    valuesIndex = 0;
                }
                values[valuesIndex] = BigDecimal.valueOf((Float)values[valuesIndex]).add(fractionalAtom).floatValue();
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

    private boolean isIntegerValue(Float value) {
        return BigDecimal.valueOf(value).stripTrailingZeros().scale() <= 0;
    }

}
