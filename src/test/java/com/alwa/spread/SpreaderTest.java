package com.alwa.spread;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SpreaderTest {

    Product PRODUCT_ONE = new Product("ALWA1", BigDecimal.valueOf(13.99));
    Product PRODUCT_TWO = new Product("ALWA2", BigDecimal.valueOf(5.99));
    Product PRODUCT_THREE = new Product("ALWA3", BigDecimal.valueOf(24.99));

    Spread<Product> THREE_PRODUCTS =
        SpreadUtil.sequence(PRODUCT_ONE, PRODUCT_TWO, PRODUCT_THREE);

    Spread<Integer> VARIABLE_QUANTITIES = SpreadUtil.sequence(1, 2, 3);

    private LocalDateTime WEEK_START = LocalDateTime.MIN;

    private Spread<Instant> EVERY_HOUR =
        SpreadUtil
            .initial(WEEK_START)
            .step(dateTime -> dateTime.plusHours(1))
            .map(dateTime -> dateTime.toInstant(ZoneOffset.UTC));

    private Spread<BigDecimal> tenThousandKws = SpreadUtil.cumulative(BigDecimal.valueOf(10000));

    private final Spread<List<OrderLine>> ORDER_LINES =
        SpreadUtil.list(
            new Spreader<OrderLine>()
                .factory(() -> new OrderLine(Spread.in(THREE_PRODUCTS), Spread.in(VARIABLE_QUANTITIES)))
                .steps(3)
        );

    @Test
    public void viaConstructor() {
        Spread<Instant> everyHour =
                SpreadUtil
                    .initial(LocalDateTime.MIN)
                    .step(previousDate -> previousDate.plusHours(1))
                    .map(localDateTime -> localDateTime.toInstant(ZoneOffset.UTC));

        Spread<BigDecimal> cumulativeReadings =
                SpreadUtil.cumulative(
                    BigDecimal.valueOf(10000)
                );

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

        assertThat(
            readings
                .stream()
                .map(TestDataObject::getBigDecimalField)
                .reduce(BigDecimal.ZERO, BigDecimal::add))
            .isEqualTo(BigDecimal.valueOf(10000));

        readings
            .stream()
            .map(TestDataObject::getTimeField)
            .forEach(date -> assertDateInRange(date, LocalDateTime.MIN, LocalDateTime.MIN.plusHours(169)));

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

        assertThat(
            readings
                .stream()
                .map(TestDataObject::getBigDecimalField)
                .reduce(BigDecimal.ZERO, BigDecimal::add))
            .isEqualTo(BigDecimal.valueOf(10000));

        readings
            .stream()
            .map(TestDataObject::getTimeField)
            .forEach(date -> assertDateInRange(date, LocalDateTime.MIN, LocalDateTime.MIN.plusHours(169)));

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

        assertThat(
            readings
                .stream()
                .map(TestDataObject::getBigDecimalField)
                .reduce(BigDecimal.ZERO, BigDecimal::add))
            .isEqualTo(BigDecimal.valueOf(10000));

        readings
            .stream()
            .map(TestDataObject::getTimeField)
            .forEach(date -> assertDateInRange(date, LocalDateTime.MIN, LocalDateTime.MIN.plusHours(169)));

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
                    .mutator(
                        testDataObject ->
                            testDataObject
                                .setStringField(
                                    Spread.in(fixedStringValue)
                                )
                    )
                    .steps(24 * 7)
                    .spread()
                    .collect(Collectors.toList());

        assertThat(readings.size()).isEqualTo(24 * 7);

        assertThat(
            readings
                .stream()
                .map(TestDataObject::getBigDecimalField)
                .reduce(BigDecimal.ZERO, BigDecimal::add))
            .isEqualTo(BigDecimal.valueOf(10000));

        readings
            .stream()
            .map(TestDataObject::getTimeField)
            .forEach(date -> assertDateInRange(date, LocalDateTime.MIN, LocalDateTime.MIN.plusHours(169)));

        readings
            .stream()
            .map(TestDataObject::getStringField)
            .forEach(value -> assertThat(value).isEqualTo("bananna"));

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

        dataObjects
            .stream()
            .map(PrimativeTestDataObject::getIntField)
                .forEach(i -> assertThat(i).isGreaterThan(1).isLessThan(170));

        dataObjects
            .stream()
            .map(PrimativeTestDataObject::getDoubleField)
            .forEach(i -> assertThat(i).isGreaterThan(1d).isLessThan(170d));
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

        dataObjects
            .stream()
            .map(PrimativeTestDataObject::getIntField)
            .forEach(i -> assertThat(i).isGreaterThan(1).isLessThan(170));

        dataObjects
            .stream()
            .map(PrimativeTestDataObject::getDoubleField)
            .forEach(i -> assertThat(i).isGreaterThan(1d).isLessThan(170d));
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
                            Spread.in(SpreadUtil.fixed(1)),
                            Spread.in(SpreadUtil.fixed(2d))
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
                            Spread.in(SpreadUtil.fixed(1)),
                            Spread.in(SpreadUtil.fixed(2d))
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
                .contains("Invalid Spread Object - Type:[class java.math.BigDecimal], Value:[-1]");
    }

    @Test
    public void testSequencedSpreadViaConstructor() {

        Spread<Instant> threeDates =
            SpreadUtil.sequence(
                LocalDateTime.of(2020, 1, 1, 1, 1),
                LocalDateTime.of(2020, 1, 2, 1, 1),
                LocalDateTime.of(2020, 1, 3, 1, 1)
            ).map(localDateTime -> localDateTime.toInstant(ZoneOffset.UTC));

        List<TestDataObject> dataObjects =
            new Spreader<TestDataObject>()
                .factory(() -> new TestDataObject(Spread.in(threeDates)))
                .steps(24 * 7)
                .spread()
                .collect(Collectors.toList());

        assertThat(dataObjects.size()).isEqualTo(24 * 7);

        dataObjects
            .stream()
            .map(TestDataObject::getTimeField)
            .forEach(date ->
                assertIsOneOf(
                    date,
                    LocalDateTime.of(2020, 1, 1, 1, 1),
                    LocalDateTime.of(2020, 1, 2, 1, 1),
                    LocalDateTime.of(2020, 1, 3, 1, 1)
                )
            );

    }

    @Test
    public void randomStringTest() {
        Spread<String> callRandomString =
            SpreadUtil.custom((String) -> RandomStringUtils.random(7, true, true));

        List<TestDataObject> dataObjects =
            new Spreader<TestDataObject>()
                .factory(TestDataObject::new)
                .mutator(testDataObject -> testDataObject.setStringField(Spread.in(callRandomString)))
                .steps(24 * 7)
                .spread()
                .collect(Collectors.toList());

        assertThat(dataObjects.size()).isEqualTo(24 * 7);

        dataObjects
            .stream()
            .map(TestDataObject::getStringField)
            .forEach(s -> assertThat(s.length()).isEqualTo(7));
    }

    @Test
    public void primativeIntTest() {
        Spread<Integer> integerValues =
            SpreadUtil.cumulative(
                10000
            );

        List<PrimativeTestDataObject> dataObjects =
            new Spreader<PrimativeTestDataObject>()
                .factory(
                    () -> new PrimativeTestDataObject(
                        Spread.in(integerValues)
                    )
                )
                .steps(24 * 7)
                .spread()
                .collect(Collectors.toList());

        assertThat(dataObjects.size()).isEqualTo(24 * 7);

        assertThat(
            dataObjects
                .stream()
                .map(PrimativeTestDataObject::getIntField)
                .reduce(0, Integer::sum))
            .isEqualTo(Integer.valueOf(10000));
    }

    @Test
    public void bigIntegerTest() {
        Spread<BigInteger> bigIntegerValues =
            SpreadUtil.cumulative(
                BigInteger.valueOf(70000)
            );

        List<TestDataObject> dataObjects =
            new Spreader<TestDataObject>()
                .factory(TestDataObject::new)
                .mutator(
                    testDataObject -> testDataObject.setBigInteger(Spread.in(bigIntegerValues))
                )
                .steps(24 * 7)
                .spread()
                .collect(Collectors.toList());

        assertThat(dataObjects.size()).isEqualTo(24 * 7);

        assertThat(
            dataObjects
                .stream()
                .map(TestDataObject::getBigInteger)
                .reduce(BigInteger.ZERO, BigInteger::add))
            .isEqualTo(BigInteger.valueOf(70000));
    }

    @Test
    public void testBasedOn() {
        Spread<String> threeLetterSpread =
            SpreadUtil.sequence("a", "b", "c");

        Spread<Boolean> startsWithAnA =
            SpreadUtil.
                related(threeLetterSpread)
                .step(relatedValue -> relatedValue.startsWith("a"));

        List<TestDataObject> dataObjects =
            new Spreader<TestDataObject>()
                .factory(TestDataObject::new)
                .mutator(
                    testDataObject -> testDataObject.setBooleanField(Spread.in(startsWithAnA))
                )
                .steps(24 * 7)
                .spread()
                .collect(Collectors.toList());

        assertThat(dataObjects.size()).isEqualTo(24 * 7);

        IntStream.range(0, dataObjects.size())
            .forEach(
                i ->
                    assertThat(dataObjects.get(i).getBooleanField()).isEqualTo(shouldBeAnA(i + 1))
            );
    }

    private boolean shouldBeAnA(int step) {
        return step % 3 == 1;
    }

    @Test
    public void testNestedListField() {
        Spread<List<BigDecimal>> cumulativeReadingsListed =
            SpreadUtil.list(
                SpreadUtil.cumulative(BigDecimal.valueOf(70000)),
                6
            );

        List<TestDataObject> dataObjects =
            new Spreader<TestDataObject>()
                    .factory(TestDataObject::new)
                    .mutators(testDataObject ->
                        testDataObject.setListField(Spread.in(cumulativeReadingsListed)))
                    .steps(24 * 7)
                    .spread()
                    .collect(Collectors.toList());

        assertThat(dataObjects.size()).isEqualTo(24 * 7);

        assertThat(dataObjects
                    .stream()
                    .map(TestDataObject::getListField)
                    .flatMap(Collection::stream)
                    .reduce(BigDecimal.ZERO, BigDecimal::add))
            .isEqualTo(BigDecimal.valueOf(70000 * 168));
    }

    @Test
    public void testNestedSetField() {
        Spread<Set<Integer>> cumulativeReadingsSetted =
            SpreadUtil.set(
                SpreadUtil.cumulative(6),
                1
            );

        List<TestDataObject> dataObjects =
            new Spreader<TestDataObject>()
                .factory(TestDataObject::new)
                .mutators(testDataObject -> testDataObject.setSetField(Spread.in(cumulativeReadingsSetted)))
                .steps(24 * 7)
                .spread()
                .collect(Collectors.toList());

        assertThat(dataObjects.size()).isEqualTo(24 * 7);

        assertThat(dataObjects
            .stream()
            .map(TestDataObject::getSetField)
            .flatMap(Collection::stream)
            .reduce(0, Integer::sum))
            .isEqualTo(Integer.valueOf(6 * 168));
    }

    @Test
    public void testNestedMapField() {
        Spread<String> randomMapKey =
            SpreadUtil.custom((String) -> RandomStringUtils.random(7, true, true));

        Spread<Map<String, Integer>> readingsInMap =
            SpreadUtil.map(
                randomMapKey,
                SpreadUtil.cumulative(70000),
                6
            );

        List<TestDataObject> dataObjects =
            new Spreader<TestDataObject>()
                .factory(TestDataObject::new)
                .mutators(testDataObject -> testDataObject.setMapField(Spread.in(readingsInMap)))
                .steps(24 * 7)
                .spread()
                .collect(Collectors.toList());

        assertThat(dataObjects.size()).isEqualTo(24 * 7);

        assertThat(dataObjects
            .stream()
            .map(TestDataObject::getMapField)
            .map(Map::values)
            .flatMap(Collection::stream)
            .reduce(0, Integer::sum))
            .isEqualTo(Integer.valueOf(70000 * 168));
    }

    @Test
    public void publicMethodsTest() {
        Spread<BigDecimal> cumulativeReadings =
            SpreadUtil.cumulative(BigDecimal.valueOf(70000));

        List<TestDataObject> dataObjects =
            new Spreader<TestDataObject>()
                .factory(TestDataObject::new)
                .mutators(testDataObject -> testDataObject.publicBigDecimalField = Spread.in(cumulativeReadings))
                .steps(24 * 7)
                .spread()
                .collect(Collectors.toList());

        assertThat(dataObjects.size()).isEqualTo(24 * 7);

        assertThat(dataObjects
            .stream()
            .map(testDataObject -> testDataObject.publicBigDecimalField)
            .reduce(BigDecimal.ZERO, BigDecimal::add))
            .isEqualTo(BigDecimal.valueOf(70000));
    }

    @Test
    public void testDynamicSequence() {
        Spread<Integer> rangeOfIntegers =
            SpreadUtil.sequence(
                IntStream.range(0, 1000)
                    .boxed()
                    .toArray(Integer[]::new)
            );

        List<TestDataObject> dataObjects =
            new Spreader<TestDataObject>()
                .factory(TestDataObject::new)
                .mutators(testDataObject -> testDataObject.setIntegerField(Spread.in(rangeOfIntegers)))
                .steps(1000)
                .spread()
                .collect(Collectors.toList());

        assertThat(dataObjects.size()).isEqualTo(1000);

        assertThat(dataObjects
            .stream()
            .map(TestDataObject::getIntegerField)
            .reduce(0, Integer::sum))
            .isEqualTo(Integer.valueOf(499500));
    }

    @Test
    public void testNestedMapWithSpreader() {

        Spread<Integer> someIntegers = SpreadUtil.cumulative(70000);

        Spreader<AnotherTestDataObject> nestedObjectSpreader =
            new Spreader<AnotherTestDataObject>()
                .factory(AnotherTestDataObject::new)
                .mutator(anotherTestDataObject -> anotherTestDataObject.setIntField(Spread.in(someIntegers)))
                .steps(3);

        Spread<String> mapKeysSpread =
            SpreadUtil.custom((String) -> RandomStringUtils.random(7, true, true));

        Spreader<String> nestedMapKeySpreader =
            new Spreader<String>()
                .factory(() -> String.valueOf(Spread.in(mapKeysSpread)))
                .steps(3);

        Spread<Map<String, AnotherTestDataObject>> nestedObjectsMap =
            SpreadUtil.map(
                nestedMapKeySpreader,
                nestedObjectSpreader
            );

        List<TestDataObject> dataObjects =
            new Spreader<TestDataObject>()
                .factory(TestDataObject::new)
                .mutators(testDataObject -> testDataObject.setNestedObjectMapField(Spread.in(nestedObjectsMap)))
                .steps(1000)
                .spread()
                .collect(Collectors.toList());

        assertThat(dataObjects.size()).isEqualTo(1000);

        assertThat(dataObjects
            .stream()
            .map(TestDataObject::getNestedObjectMapField)
            .map(Map::values)
            .flatMap(Collection::stream)
            .map(AnotherTestDataObject::getIntField)
            .reduce(0, Integer::sum))
            .isEqualTo(Integer.valueOf(70000 * 1000));

    }

    @Test
    public void testNestedListWithSpreader() {

        Spread<Integer> someInts = SpreadUtil.cumulative(70000);

        Spreader<AnotherTestDataObject> nestedObjectSpreader =
            new Spreader<AnotherTestDataObject>()
                .factory(AnotherTestDataObject::new)
                .mutator(anotherTestDataObject -> anotherTestDataObject.setIntField(Spread.in(someInts)))
                .steps(3);

        Spread<List<AnotherTestDataObject>> nestedObjectsMap =
            SpreadUtil.list(
                nestedObjectSpreader
            );

        List<TestDataObject> dataObjects =
            new Spreader<TestDataObject>()
                .factory(TestDataObject::new)
                .mutators(testDataObject -> testDataObject.setNestedObjectListField(Spread.in(nestedObjectsMap)))
                .steps(1000)
                .spread()
                .collect(Collectors.toList());

        assertThat(dataObjects.size()).isEqualTo(1000);

        assertThat(dataObjects
            .stream()
            .map(TestDataObject::getNestedObjectListField)
            .flatMap(List::stream)
            .map(AnotherTestDataObject::getIntField)
            .reduce(0, Integer::sum))
            .isEqualTo(Integer.valueOf(70000 * 1000));

    }

    @Test
    public void testNestedSetWithSpreader() {

        Spread<Integer> someIntegers = SpreadUtil.cumulative(70000);

        Spreader<AnotherTestDataObject> nestedObjectSpreader =
            new Spreader<AnotherTestDataObject>()
                .factory(AnotherTestDataObject::new)
                .mutator(anotherTestDataObject -> anotherTestDataObject.setIntField(Spread.in(someIntegers)))
                .steps(1);

        Spread<Set<AnotherTestDataObject>> nestedObjectsMap =
            SpreadUtil.set(
                nestedObjectSpreader
            );

        List<TestDataObject> dataObjects =
            new Spreader<TestDataObject>()
                .factory(TestDataObject::new)
                .mutators(testDataObject -> testDataObject.setNestedObjectSetField(Spread.in(nestedObjectsMap)))
                .steps(1000)
                .spread()
                .collect(Collectors.toList());

        assertThat(dataObjects.size()).isEqualTo(1000);

        assertThat(dataObjects
            .stream()
            .map(TestDataObject::getNestedObjectSetField)
            .flatMap(Set::stream)
            .map(AnotherTestDataObject::getIntField)
            .reduce(0, Integer::sum))
            .isEqualTo(Integer.valueOf(70000 * 1000));

    }

    private void assertDateInRange(Instant instant, LocalDateTime lowerBound, LocalDateTime upperBound) {
        assertThat(LocalDateTime.ofInstant(instant, ZoneId.systemDefault())).isAfterOrEqualTo(lowerBound);
        assertThat(LocalDateTime.ofInstant(instant, ZoneId.systemDefault())).isBefore(upperBound);
    }

    private void assertIsOneOf(Instant date, LocalDateTime... examples) {
        assertThat(examples).contains(LocalDateTime.ofInstant(date, ZoneId.systemDefault()));
    }

    @Test
    public void testSpreadsDefinedInClasLevelConstants() {
        List<TestDataObject> READINGS_ACROSS_WEEK =
            new Spreader<TestDataObject>()
                .factory(() -> new TestDataObject(Spread.in(EVERY_HOUR), Spread.in(tenThousandKws)))
                .steps(168)
                .debug()
                .spread()
                .collect(Collectors.toList());

        assertThat(READINGS_ACROSS_WEEK.size()).isEqualTo(168);

        assertThat(
            READINGS_ACROSS_WEEK
                .stream()
                .map(TestDataObject::getBigDecimalField)
                .reduce(BigDecimal.ZERO, BigDecimal::add))
            .isEqualTo(BigDecimal.valueOf(10000));

        READINGS_ACROSS_WEEK
            .stream()
            .map(TestDataObject::getTimeField)
            .forEach(date -> assertDateInRange(date, WEEK_START, WEEK_START.plusHours(169)));
    }

    @Test
    public void testOrderLinesTotalUpToCorrectPrice() {

        Spread<String> CUSTOMER_ID = SpreadUtil.fixed("ALWA123");

        Order ORDER =
            new Spreader<Order>()
                .factory(() -> new Order(Spread.in(CUSTOMER_ID), Spread.in(ORDER_LINES)))
                .steps(1)
                .spread()
                .collect(Collectors.toList())
                .get(0);

        assertThat(ORDER.getOrderTotal()).isEqualTo(BigDecimal.valueOf(100.94));
    }

}