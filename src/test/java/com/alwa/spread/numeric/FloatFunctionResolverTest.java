package com.alwa.spread.numeric;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FloatFunctionResolverTest {;

    private final FloatFunctionResolver floatFunctionResolver = new FloatFunctionResolver();
    private final Float seed = 10000f;
    private final RoundingMode roundingMode = RoundingMode.HALF_EVEN;
    private final BigDecimal fractionalAtom = BigDecimal.valueOf(0.01);

    @Test
    public void simpleCumulativeTest() {
        assertThat(
            floatFunctionResolver.getStepFunction(1, 1, seed, roundingMode, fractionalAtom).apply(seed)
        ).isEqualTo(10000.0f);

        assertThat(
            floatFunctionResolver
                .getStepFunction(2, 2, seed, roundingMode, fractionalAtom)
                .apply(seed)
        ).isEqualTo(5000.0f);
    }

    @Test
    public void manyValuesTest() {
        Float largeSeed = 500000f;
        Float total =
            IntStream.range(1, 499)
                .mapToObj(i ->
                    floatFunctionResolver
                        .getStepFunction(498, i, largeSeed, roundingMode, fractionalAtom)
                        .apply(largeSeed)
                )
                .map(i -> ((Float) i))
                .reduce(0f, Float::sum);
        assertThat(total).isEqualTo(largeSeed);
    }

    @Test
    public void manyValuesFractionalTest() {
        Float largeSeed = 500000.342f;
        Float total =
            IntStream.range(1, 499)
                .mapToObj(i ->
                    floatFunctionResolver
                        .getStepFunction(498, i, largeSeed, roundingMode, fractionalAtom)
                        .apply(largeSeed)
                )
                .map(i -> ((Float) i))
                .reduce(0f, Float::sum);
        assertThat(BigDecimal.valueOf(total).setScale(3, roundingMode).floatValue()).isEqualTo(largeSeed);
    }

    @Test
    public void manyValuesMoreStepsThanFractionalTotal() {
        Float smallSeed = 1.40f;
        Float total =
            IntStream.range(1, 78601)
                .mapToObj(i ->
                    floatFunctionResolver
                        .getStepFunction(78600, i, smallSeed, roundingMode, fractionalAtom)
                        .apply(smallSeed)
                )
                .map(i -> ((Float) i))
                .reduce(0f, Float::sum);
        assertThat(BigDecimal.valueOf(total).setScale(3, roundingMode).floatValue()).isEqualTo(smallSeed);
    }

    @Test
    public void manyValuesMoreStepsThanIntegerTotal() {
        Float smallSeed = 17f;
        Float total =
            IntStream.range(1, 78601)
                .mapToObj(i ->
                    floatFunctionResolver
                        .getStepFunction(78600, i, smallSeed, roundingMode, fractionalAtom)
                        .apply(smallSeed)
                )
                .map(i -> ((Float) i))
                .reduce(0f, Float::sum);
        assertThat(total).isEqualTo(smallSeed);
    }

    @Test
    public void testValidation() {
        assertThat(floatFunctionResolver.validateSeed((double) -1)).isFalse();
        assertThat(floatFunctionResolver.validateSeed((double) 0)).isFalse();
        assertThat(floatFunctionResolver.validateSeed(null)).isFalse();
        assertThat(floatFunctionResolver.validateSeed(50.0)).isTrue();
    }

}
