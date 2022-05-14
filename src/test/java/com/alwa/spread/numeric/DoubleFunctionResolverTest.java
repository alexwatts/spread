package com.alwa.spread.numeric;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DoubleFunctionResolverTest {;

    private final DoubleFunctionResolver doubleFunctionResolver = new DoubleFunctionResolver();
    private final Double seed = 10000d;
    private final RoundingMode roundingMode = RoundingMode.HALF_EVEN;
    private final BigDecimal fractionalAtom = BigDecimal.valueOf(0.0001);

    @Test
    public void simpleCumulativeTest() {
        assertThat(
            doubleFunctionResolver.getStepFunction(1, 1, seed, roundingMode, fractionalAtom).apply(seed)
        ).isEqualTo(10000.0);

        assertThat(
            doubleFunctionResolver
                .getStepFunction(2, 2, seed, roundingMode, fractionalAtom)
                .apply(seed)
        ).isEqualTo(5000.0);
    }

    @Test
    public void manyValuesTest() {
        Double largeSeed = 500000d;
        Double total =
            IntStream.range(1, 499)
                .mapToObj(i ->
                    doubleFunctionResolver
                        .getStepFunction(498, i, largeSeed, roundingMode, fractionalAtom)
                        .apply(largeSeed)
                )
                .map(i -> ((Double) i))
                .reduce(0d, Double::sum);
        assertThat(total).isEqualTo(largeSeed);
    }

    @Test
    public void manyValuesFractionalTest() {
        Double largeSeed = 500000.342d;
        Double total =
            IntStream.range(1, 499)
                .mapToObj(i ->
                    doubleFunctionResolver
                        .getStepFunction(498, i, largeSeed, roundingMode, fractionalAtom)
                        .apply(largeSeed)
                )
                .map(i -> ((Double) i))
                .reduce(0d, Double::sum);
        assertThat(BigDecimal.valueOf(total).setScale(3, roundingMode).doubleValue()).isEqualTo(largeSeed);
    }

    @Test
    public void manyValuesMoreStepsThanFractionalTotal() {
        Double smallSeed = 1.342d;
        Double total =
            IntStream.range(1, 78601)
                .mapToObj(i ->
                    doubleFunctionResolver
                        .getStepFunction(78600, i, smallSeed, roundingMode, fractionalAtom)
                        .apply(smallSeed)
                )
                .map(i -> ((Double) i))
                .reduce(0d, Double::sum);
        assertThat(BigDecimal.valueOf(total).setScale(3, roundingMode).doubleValue()).isEqualTo(smallSeed);
    }

    @Test
    public void manyValuesMoreStepsThanIntegerTotal() {
        Double smallSeed = 17d;
        Double total =
            IntStream.range(1, 78601)
                .mapToObj(i ->
                    doubleFunctionResolver
                        .getStepFunction(78600, i, smallSeed, roundingMode, fractionalAtom)
                        .apply(smallSeed)
                )
                .map(i -> ((Double) i))
                .reduce(0d, Double::sum);
        assertThat(total).isEqualTo(smallSeed);
    }

    @Test
    public void testValidation() {
        assertThat(doubleFunctionResolver.validateSeed((double) -1)).isFalse();
        assertThat(doubleFunctionResolver.validateSeed((double) 0)).isFalse();
        assertThat(doubleFunctionResolver.validateSeed(null)).isFalse();
        assertThat(doubleFunctionResolver.validateSeed(50.0)).isTrue();
    }

}
