package com.alwa.spread;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class SupportedFunctionResolvers {

    public static Map<Class, StepFunctionResolver> supportedFunctionResolvers() {
            Map<Class, StepFunctionResolver> resolverMap = new HashMap<>();
            resolverMap.put(BigDecimal.class, new BigDecimalFunctionResolver());
            resolverMap.put(BigInteger.class, new BigIntegerFunctionResolver());
            resolverMap.put(Double.class, new DoubleFunctionResolver());
            resolverMap.put(Long.class, new LongFunctionResolver());
            resolverMap.put(Integer.class, new IntegerFunctionResolver());
            return resolverMap;
    }

}
