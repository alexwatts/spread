package com.alwa.spread;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ShortFunctionResolverTest {;

    private ShortFunctionResolver shortFunctionResolver = new ShortFunctionResolver();
    private Short seed = 400;
    private RoundingMode roundingMode = RoundingMode.HALF_EVEN;
    private final BigDecimal fractionalAtom = BigDecimal.valueOf(0.01);

    @Test
    public void simpleCumulativeTest() {
        assertThat(
            shortFunctionResolver.getStepFunction(1, 1, seed, roundingMode, fractionalAtom).apply(seed)
        ).isEqualTo(400);

        assertThat(
            shortFunctionResolver.getStepFunction(2, 2, seed, roundingMode, fractionalAtom).apply(seed)
        ).isEqualTo(200);
    }

    @Test
    public void manyValuesTest() {
        short largeSeed = 20000;
        Short total =
            IntStream.range(1, 499)
                .mapToObj(i ->
                    shortFunctionResolver
                        .getStepFunction(498, i, largeSeed, roundingMode, fractionalAtom)
                        .apply(largeSeed)
                )
                .map(i -> ((Integer) i))
                .reduce(0, Integer::sum)
                .shortValue();
        assertThat(total).isEqualTo(largeSeed);
    }

    @Test
    public void testValidation() {
        assertThat(shortFunctionResolver.validateSeed((short)-1)).isFalse();
        assertThat(shortFunctionResolver.validateSeed((short)0)).isFalse();
        assertThat(shortFunctionResolver.validateSeed(null)).isFalse();
        assertThat(shortFunctionResolver.validateSeed((short)50)).isTrue();
    }

}
