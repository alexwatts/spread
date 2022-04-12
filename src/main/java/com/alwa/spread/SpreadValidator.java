package com.alwa.spread;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SpreadValidator {

    private static List<Class> supportedCumulativeClasses = Arrays.asList(
            BigDecimal.class,
            Integer.class
    );

    public static void validateCumulativeSpread(Object cumulativeObject) {
        validateCumulativeObjectType(cumulativeObject);
        validateCumulativeObjectValue(cumulativeObject);
    }

    public static void validateCumulativeObjectType(Object cumulativeObject) {
        if (!supportedCumulativeClasses.contains(cumulativeObject.getClass())) {
            throw new SpreadException(unsupportedCumulativeObjectTypeMessage(cumulativeObject));
        }
    }

    public static void validateCumulativeObjectValue(Object cumulativeObject) {
        RangeResolver rangeResolver = new RangeResolver(cumulativeObject);
        if (!rangeResolver.validateSeed()) {
            String message = "Invalid Spread Object - Type:[%s], Value:[%s]";
            throw new SpreadException(String.format(message, cumulativeObject.getClass(), cumulativeObject));
        }
    }

    private static String unsupportedCumulativeObjectTypeMessage(Object cumulativeObject) {
        String messageFormat = "Unsupported Cumulative Spread Object - Type:[%s], Value:[%s] \r\n" +
                "Supported Object Types: [%s]";

        throw new SpreadException(
                String.format(
                        messageFormat,
                        cumulativeObject.getClass(),
                        cumulativeObject,
                        supportedCumulativeClasses
                                .stream()
                                .map(Class::getName)
                                .collect(Collectors.joining(" "))
                        )
        );
    }

}
