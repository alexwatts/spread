package com.alwa.spread;

import org.junit.jupiter.api.Test;

import java.math.RoundingMode;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class IntegerFunctionResolverTest {;

    private IntegerFunctionResolver integerFunctionResolver = new IntegerFunctionResolver();
    private Integer seed = 400;
    private RoundingMode roundingMode = RoundingMode.HALF_EVEN;

    @Test
    public void simpleCumulativeTest() {
        assertThat(
                integerFunctionResolver.getStepFunction(1, 1, seed, roundingMode).apply(seed)
        ).isEqualTo(Integer.valueOf(400));

        assertThat(
                integerFunctionResolver.getStepFunction(2, 2, seed, roundingMode).apply(seed)
        ).isEqualTo(Integer.valueOf(200));
    }

}
