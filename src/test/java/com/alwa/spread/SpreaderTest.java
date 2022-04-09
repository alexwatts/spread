package com.alwa.spread;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SpreaderTest {

    @Test
    public void viaConstructor() {
        Spread<Instant> everyHour =
                SpreadUtil
                    .initial(LocalDateTime.MIN)
                    .step(previousDate -> previousDate.plusHours(1))
                    .map(localDateTime -> localDateTime.toInstant(ZoneOffset.UTC));

        Spread<BigDecimal> cumulativeReadings =
                SpreadUtil.cumulative(BigDecimal.valueOf(10000), RoundingMode.HALF_DOWN);

        List<TestDataObject> readings =
            new Spreader<TestDataObject>()
                .factory(
                    () -> new TestDataObject(
                        Spread.in(everyHour),
                        Spread.in(cumulativeReadings)
                    )
                )
                .steps(24 * 7)
                .spread()
                .collect(Collectors.toList());

        assertThat(readings.size()).isEqualTo(24 * 7);
    }

    @Test
    public void viaFactoryMethod() {
        Spread<Instant> everyHour =
                SpreadUtil
                    .initial(LocalDateTime.MIN)
                    .step(previousDate -> previousDate.plusHours(1))
                    .map(localDateTime -> localDateTime.toInstant(ZoneOffset.UTC));

        Spread<BigDecimal> cumulativeReadings =
                SpreadUtil.cumulative(BigDecimal.valueOf(10000));

        List<TestDataObject> readings =
                new Spreader<TestDataObject>()
                        .factory(
                            () -> TestDataObject.newInstance(
                                Spread.in(everyHour),
                                Spread.in(cumulativeReadings)
                            )
                        )
                        .steps(24 * 7)
                        .spread()
                        .collect(Collectors.toList());

        assertThat(readings.size()).isEqualTo(24 * 7);
    }

    @Test
    public void valuesViaMutators() {
        Spread<Instant> everyHour =
                SpreadUtil
                    .initial(LocalDateTime.MIN)
                    .step(previousDate -> previousDate.plusHours(1))
                    .map(localDateTime -> localDateTime.toInstant(ZoneOffset.UTC));

        Spread<BigDecimal> cumulativeReadings =
                SpreadUtil.cumulative(BigDecimal.valueOf(10000));

        List<TestDataObject> readings =
            new Spreader<TestDataObject>()
                .factory(TestDataObject::new)
                .mutators(
                        testDataObject -> testDataObject.setTimeField(Spread.in(everyHour)),
                        testDataObject -> testDataObject.setBigDecimalField(Spread.in(cumulativeReadings))
                )
                .steps(24 * 7)
                .spread()
                .collect(Collectors.toList());

        assertThat(readings.size()).isEqualTo(24 * 7);
    }

    @Test
    public void constructorAndMutator() {
        Spread<Instant> everyHour =
            SpreadUtil
            .initial(LocalDateTime.MIN)
            .step(previousDate -> previousDate.plusHours(1))
            .map(localDateTime -> localDateTime.toInstant(ZoneOffset.UTC));

        Spread<BigDecimal> cumulativeReadings =
                SpreadUtil.cumulative(BigDecimal.valueOf(10000));

        Spread<String> fixedStringValue =
                SpreadUtil.fixed("bananna");

        List<TestDataObject> readings =
            new Spreader<TestDataObject>()
                    .factory(TestDataObject::new)
                    .factory(
                            () -> new TestDataObject(
                                    Spread.in(everyHour),
                                    Spread.in(cumulativeReadings)
                            )
                    )
                    .mutator(testDataObject -> testDataObject.setStringField(Spread.in(fixedStringValue)))
                    .steps(24 * 7)
                    .spread()
                    .collect(Collectors.toList());

        assertThat(readings.size()).isEqualTo(24 * 7);
    }

    @Test
    public void primitiveConstructor() {
        Spread<Integer> everyInt =
                SpreadUtil
                    .initial(1)
                    .step(previousInt -> previousInt + 1);

        Spread<Double> someDoubles =
                SpreadUtil
                    .initial(0.1d)
                    .step(previousDouble -> previousDouble + 1);

        List<PrimativeTestDataObject> dataObjects =
                new Spreader<PrimativeTestDataObject>()
                        .factory(
                            () -> new PrimativeTestDataObject(
                                    Spread.in(everyInt),
                                    Spread.in(someDoubles)
                            )
                        )
                        .steps(24 * 7)
                        .spread()
                        .collect(Collectors.toList());

        assertThat(dataObjects.size()).isEqualTo(24 * 7);
    }

    @Test
    public void fixedPrimitiveConstructor() {
        Spread<Integer> everyInt =
                SpreadUtil
                    .initial(1)
                    .step(previousInt -> previousInt + 1);

        Spread<Double> fixedDouble =
                SpreadUtil
                    .fixed(1.6d);

        List<PrimativeTestDataObject> dataObjects =
                new Spreader<PrimativeTestDataObject>()
                        .factory(
                            () -> new PrimativeTestDataObject(
                                Spread.in(everyInt),
                                Spread.in(fixedDouble)
                            )
                        )
                        .steps(24 * 7)
                        .spread()
                        .collect(Collectors.toList());

        assertThat(dataObjects.size()).isEqualTo(24 * 7);
    }

    @Test
    public void testUnsupportedCumulativeClassValidation() {
        SpreadException thrown = assertThrows(
                SpreadException.class,
                () -> SpreadUtil.cumulative(new TestDataObject()),
                "Expected SpreadException because cumulative class isn't supported"
        );
        assertThat(thrown.getMessage())
                .contains("Unsupported Cumulative Spread Object - Type:[class com.alwa.spread.TestDataObject]");
    }

    @Test
    public void testMissingStepsThrowsValidation() {
        SpreaderException thrown = assertThrows(
                SpreaderException.class,
                () -> new Spreader<PrimativeTestDataObject>()
                        .factory(
                            () -> new PrimativeTestDataObject(
                                Spread.in(new FixedSpread<>(1)),
                                Spread.in(new FixedSpread<>(2d))
                            )
                        )
                        .spread(),
                "Expected SpreaderException because steps method was missing"
        );
        assertThat(thrown.getMessage())
                .contains("Spreader spread() failure, missing steps. You may need to add a step definition to define how many objects to spread.");
    }

    @Test
    public void testNegativeStepsThrowsValidation() {
        SpreaderException thrown = assertThrows(
                SpreaderException.class,
                () -> new Spreader<PrimativeTestDataObject>()
                    .factory(
                        () -> new PrimativeTestDataObject(
                            Spread.in(new FixedSpread<>(1)),
                            Spread.in(new FixedSpread<>(2d))
                        )
                    )
                    .steps(-1)
                    .spread(),
                "Expected SpreaderException because steps method was missing"
        );
        assertThat(thrown.getMessage())
                .contains("Spreader spread() failure, steps Invalid. Steps must be defined as a positive integer and defines how many objects to spread. Invalid Steps: [-1]");
    }

    @Test
    public void testMissingFactoryThrowsValidation() {

        SpreaderException thrown = assertThrows(
                SpreaderException.class,
                () -> new Spreader<PrimativeTestDataObject>()
                        .steps(24 * 7)
                        .spread(),
                "Expected SpreaderException because factory was missing"
        );
        assertThat(thrown.getMessage())
                .contains("Spreader spread() failure, missing factory. " +
                        "You may need to add a factory to call a constructor, or a factory method, to create instances.");
    }

    @Test
    public void testNegativeCumulativeSpreadThrowsValidation() {
        SpreadException thrown = assertThrows(
                SpreadException.class,
                () -> SpreadUtil.cumulative(BigDecimal.valueOf(-1)),
                "Expected SpreadException because cumulative value is negative"
        );
        assertThat(thrown.getMessage())
                .contains("Invalid Spread Object - Type:[class java.math.BigDecimal], Value: -1]");
    }

}