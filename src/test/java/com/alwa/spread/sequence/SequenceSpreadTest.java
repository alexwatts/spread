package com.alwa.spread.sequence;

import com.alwa.spread.core.Spread;
import com.alwa.spread.SpreadUtil;
import com.alwa.spread.Spreader;
import com.alwa.spread.model.TestDataObject;
import com.alwa.spread.annotations.Embed;
import com.alwa.spread.annotations.In;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SequenceSpreadTest {

    @In
    private final Spread<Instant> threeDates =
        SpreadUtil.sequence(
            LocalDateTime.of(2020, 1, 1, 1, 1),
            LocalDateTime.of(2020, 1, 2, 1, 1),
            LocalDateTime.of(2020, 1, 3, 1, 1)
        ).map(localDateTime -> localDateTime.toInstant(ZoneOffset.UTC));

    @In
    private final Spread<Integer> rangeOfIntegers =
        SpreadUtil.sequence(
            IntStream.range(0, 1000)
                .boxed()
                .toArray(Integer[]::new)
        );

    @In
    @Embed(clazz = List.class, steps = 20)
    private final Spread<BigDecimal> SEQUENCED_CUMULATIVES =
        SpreadUtil.sequence(
            SpreadUtil.cumulative(BigDecimal.valueOf(10000)),
            SpreadUtil.cumulative(BigDecimal.valueOf(20000)),
            SpreadUtil.cumulative(BigDecimal.valueOf(30000)),
            SpreadUtil.cumulative(BigDecimal.valueOf(40000)),
            SpreadUtil.cumulative(BigDecimal.valueOf(50000))
        );


    @BeforeEach
    public void setUp() {
        SpreadUtil.initPackage(
            this,
            this.getClass().getPackage().getName()
        );
    }

    @Test
    public void testSequencedSpreadViaConstructor() {
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
    public void testDynamicSequence() {

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
    public void testVarargsSequencedSpreads() {
        List<TestDataObject> dataObjects =
            new Spreader<TestDataObject>()
                .factory(TestDataObject::new)
                .mutators(testDataObject ->
                    testDataObject.setListField(
                        (List<BigDecimal>)Spread.embed(SEQUENCED_CUMULATIVES)
                    )
                )
                .steps(5)
                .spread()
                .collect(Collectors.toList());

        assertThat(
            dataObjects
                .stream()
                .map(TestDataObject::getListField)
                .flatMap(List::stream)
                .reduce(BigDecimal.ZERO, BigDecimal::add))
            .isEqualTo(BigDecimal.valueOf(150000));
    }

    private void assertIsOneOf(Instant date, LocalDateTime... examples) {
        assertThat(examples).contains(LocalDateTime.ofInstant(date, ZoneId.systemDefault()));
    }

}