package com.alwa.spread.numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;

public class BigIntegerFunctionResolver extends StepFunctionResolver {

    @Override
    public Object[] initialiseValuesMap(
        String valuesMapKey,
        int totalSteps,
        Object example,
        RoundingMode roundingMode,
        BigDecimal fractionalAtom) {

            Object[] values = new Object[totalSteps];
            BigInteger seed = (BigInteger) example;
            int baseValue = seed.intValue() / totalSteps;
            Arrays.fill(values, BigInteger.valueOf(baseValue));
            int step = baseValue * totalSteps;
            for (int i = 0; i < ((BigInteger) example).intValue() - step; i++) {
                values[i] = ((BigInteger) values[i]).add(BigInteger.ONE);
            }
            valuesMap.put(valuesMapKey, values);
            return valuesMap.get(valuesMapKey);
    }

    @Override
    public boolean validateSeed(Object seed) {
        return seed != null && ((BigInteger) seed).compareTo(BigInteger.ZERO) > 0;
    }

}