package com.alwa.spread.numeric;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LongFunctionResolverTest {;

    private LongFunctionResolver longFunctionResolver = new LongFunctionResolver();
    private Long seed = 800L;
    private RoundingMode roundingMode = RoundingMode.HALF_EVEN;
    private final BigDecimal fractionalAtom = BigDecimal.valueOf(0.01);

    @Test
    public void simpleCumulativeTest() {
        assertThat(
            longFunctionResolver.getStepFunction(1, 1, seed, roundingMode, fractionalAtom).apply(seed)
        ).isEqualTo(800L);

        assertThat(
            longFunctionResolver.getStepFunction(2, 2, seed, roundingMode, fractionalAtom).apply(seed)
        ).isEqualTo(400L);
    }

    @Test
    public void manyValuesTest() {
        Long largeSeed = 20000L;
        Long total =
            IntStream.range(1, 499)
                .mapToObj(i ->
                    longFunctionResolver
                        .getStepFunction(498, i, largeSeed, roundingMode, fractionalAtom)
                        .apply(largeSeed)
                )
                .map(i -> ((Long) i))
                .reduce(0L, Long::sum);
        assertThat(total).isEqualTo(largeSeed);
    }

    @Test
    public void testValidation() {
        assertThat(longFunctionResolver.validateSeed(-1L)).isFalse();
        assertThat(longFunctionResolver.validateSeed(0L)).isFalse();
        assertThat(longFunctionResolver.validateSeed(null)).isFalse();
        assertThat(longFunctionResolver.validateSeed(50L)).isTrue();
    }

}
