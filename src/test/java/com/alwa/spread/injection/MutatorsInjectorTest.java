package com.alwa.spread.injection;

import com.alwa.spread.Spread;
import com.alwa.spread.SpreadUtil;
import com.alwa.spread.Spreader;
import com.alwa.spread.model.TestDataObject;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MutatorsInjectorTest {

    @In
    private final Spread<Instant> EVERY_HOUR =
        SpreadUtil
            .initial(LocalDateTime.MIN)
            .step(previousDate -> previousDate.plusHours(1))
            .map(localDateTime -> localDateTime.toInstant(ZoneOffset.UTC));

    @In
    private final Spread<BigDecimal> HOURLY_READINGS_OF_TOTAL_10000 =
        SpreadUtil.cumulative(BigDecimal.valueOf(10000));

    @BeforeEach
    public void setUp() {
        SpreadUtil.initPackage(
            this,
            this.getClass().getPackage().getName()
        );
    }

    @Test
    public void valuesViaMutators() {

        List<TestDataObject> readings =
            new Spreader<TestDataObject>()
                .factory(TestDataObject::new)
                .mutators(
                    testDataObject -> testDataObject.setTimeField(Spread.in(EVERY_HOUR)),
                    testDataObject -> testDataObject.setBigDecimalField(Spread.in(HOURLY_READINGS_OF_TOTAL_10000))
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

    private void assertDateInRange(Instant instant, LocalDateTime lowerBound, LocalDateTime upperBound) {
        assertThat(LocalDateTime.ofInstant(instant, ZoneId.systemDefault())).isAfterOrEqualTo(lowerBound);
        assertThat(LocalDateTime.ofInstant(instant, ZoneId.systemDefault())).isBefore(upperBound);
    }

}