package com.alwa.spread;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class BigDecimalFunctionResolverTest {;

    private final BigDecimalFunctionResolver bigDecimalFunctionResolver = new BigDecimalFunctionResolver();
    private final BigDecimal seed = BigDecimal.valueOf(10000);
    private final RoundingMode roundingMode = RoundingMode.HALF_EVEN;

    @Test
    public void simpleCumulativeTest() {
        assertThat(
                bigDecimalFunctionResolver.getStepFunction(1, 1, seed, roundingMode).apply(seed)
        ).isEqualTo(BigDecimal.valueOf(10000));

        assertThat(
                bigDecimalFunctionResolver.getStepFunction(2, 2, seed, roundingMode).apply(seed)
        ).isEqualTo(BigDecimal.valueOf(5000));
    }

}
