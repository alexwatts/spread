package com.alwa.spread;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class BigIntegerFunctionResolverTest {;

    private BigIntegerFunctionResolver bigIntegerFunctionResolver = new BigIntegerFunctionResolver();
    private BigInteger seed = BigInteger.valueOf(650);
    private RoundingMode roundingMode = RoundingMode.HALF_EVEN;
    private final BigDecimal fractionalAtom = BigDecimal.valueOf(0.01);

    @Test
    public void simpleCumulativeTest() {
        assertThat(
            bigIntegerFunctionResolver.getStepFunction(1, 1, seed, roundingMode, fractionalAtom).apply(seed)
        ).isEqualTo(BigInteger.valueOf(650));

        assertThat(
            bigIntegerFunctionResolver.getStepFunction(2, 2, seed, roundingMode, fractionalAtom).apply(seed)
        ).isEqualTo(BigInteger.valueOf(325));
    }

    @Test
    public void manyValuesTest() {
        BigInteger largeSeed = BigInteger.valueOf(20000);
        BigInteger total =
            IntStream.range(1, 499)
                .mapToObj(i ->
                    bigIntegerFunctionResolver
                        .getStepFunction(498, i, largeSeed, roundingMode, fractionalAtom)
                        .apply(largeSeed)
                )
                .map(i -> ((BigInteger) i))
                .reduce(BigInteger.ZERO, BigInteger::add);
        assertThat(total).isEqualTo(largeSeed);
    }

    @Test
    public void testValidation() {
        assertThat(bigIntegerFunctionResolver.validateSeed(BigInteger.valueOf(-1))).isFalse();
        assertThat(bigIntegerFunctionResolver.validateSeed(BigInteger.valueOf(0))).isFalse();
        assertThat(bigIntegerFunctionResolver.validateSeed(null)).isFalse();
        assertThat(bigIntegerFunctionResolver.validateSeed(BigInteger.valueOf(50))).isTrue();
    }

}
