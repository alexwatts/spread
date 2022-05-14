package com.alwa.spread.numeric;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class BigDecimalFunctionResolverTest {;

    private final BigDecimalFunctionResolver bigDecimalFunctionResolver = new BigDecimalFunctionResolver();
    private final BigDecimal seed = BigDecimal.valueOf(10000);
    private final RoundingMode roundingMode = RoundingMode.HALF_EVEN;
    private final BigDecimal fractionalAtom = BigDecimal.valueOf(0.0001);

    @Test
    public void simpleCumulativeTest() {
        assertThat(
                bigDecimalFunctionResolver.getStepFunction(1, 1, seed, roundingMode, fractionalAtom).apply(seed)
        ).isEqualTo(BigDecimal.valueOf(10000));

        assertThat(
                bigDecimalFunctionResolver
                        .getStepFunction(2, 2, seed, roundingMode, fractionalAtom)
                        .apply(seed)
        ).isEqualTo(BigDecimal.valueOf(5000));
    }

    @Test
    public void manyValuesTest() {
        BigDecimal largeSeed = BigDecimal.valueOf(500000);
        BigDecimal total =
            IntStream.range(1, 499)
                .mapToObj(i ->
                    bigDecimalFunctionResolver
                        .getStepFunction(498, i, largeSeed, roundingMode, fractionalAtom)
                        .apply(largeSeed)
                )
                .map(i -> ((BigDecimal) i))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(total).isEqualTo(largeSeed);
    }

    @Test
    public void manyValuesFractionalTest() {
        BigDecimal largeSeed = BigDecimal.valueOf(500000.342);
        BigDecimal total =
                IntStream.range(1, 499)
                    .mapToObj(i ->
                        bigDecimalFunctionResolver
                            .getStepFunction(498, i, largeSeed, roundingMode, fractionalAtom)
                            .apply(largeSeed)
                    )
                    .map(i -> ((BigDecimal) i))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(total.setScale(3, roundingMode)).isEqualTo(largeSeed);
    }

    @Test
    public void manyValuesMoreStepsThanFractionalTotal() {
        BigDecimal smallSeed = BigDecimal.valueOf(1.342);
        BigDecimal total =
                IntStream.range(1, 78601)
                    .mapToObj(i ->
                        bigDecimalFunctionResolver
                            .getStepFunction(78600, i, smallSeed, roundingMode, fractionalAtom)
                            .apply(smallSeed)
                    )
                    .map(i -> ((BigDecimal) i))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(total.setScale(3, roundingMode)).isEqualTo(smallSeed);
    }

    @Test
    public void manyValuesMoreStepsThanIntegerTotal() {
        BigDecimal smallSeed = BigDecimal.valueOf(17);
        BigDecimal total =
                IntStream.range(1, 78601)
                    .mapToObj(i ->
                        bigDecimalFunctionResolver
                            .getStepFunction(78600, i, smallSeed, roundingMode, fractionalAtom)
                            .apply(smallSeed)
                    )
                    .map(i -> ((BigDecimal) i))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(total).isEqualTo(smallSeed.stripTrailingZeros());
    }

    @Test
    public void testValidation() {
        assertThat(bigDecimalFunctionResolver.validateSeed(BigDecimal.valueOf(-1))).isFalse();
        assertThat(bigDecimalFunctionResolver.validateSeed(BigDecimal.valueOf(0))).isFalse();
        assertThat(bigDecimalFunctionResolver.validateSeed(null)).isFalse();
        assertThat(bigDecimalFunctionResolver.validateSeed(BigDecimal.valueOf(50))).isTrue();
    }

}
