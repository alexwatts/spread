package com.alwa.spread;

import org.junit.jupiter.api.Test;

import java.math.RoundingMode;
import java.util.stream.IntStream;

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

    @Test
    public void manyValuesTest() {
        int largeSeed = 20000;
        Integer total =
            IntStream.range(1, 499)
                .mapToObj(i ->
                        integerFunctionResolver
                                .getStepFunction(498, i, largeSeed, roundingMode)
                                .apply(largeSeed)
                )
                .map(i -> ((Integer) i))
                .reduce(0, Integer::sum);
        assertThat(total).isEqualTo(largeSeed);
    }

    @Test
    public void testValidation() {
        assertThat(integerFunctionResolver.validateSeed(-1)).isFalse();
        assertThat(integerFunctionResolver.validateSeed(0)).isFalse();
        assertThat(integerFunctionResolver.validateSeed(null)).isFalse();
        assertThat(integerFunctionResolver.validateSeed(50)).isTrue();
    }

}
