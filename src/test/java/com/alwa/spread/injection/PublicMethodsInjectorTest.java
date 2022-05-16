package com.alwa.spread.injection;

import com.alwa.spread.core.Spread;
import com.alwa.spread.SpreadUtil;
import com.alwa.spread.Spreader;
import com.alwa.spread.model.TestDataObject;
import com.alwa.spread.annotations.In;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PublicMethodsInjectorTest {

    @In
    private final Spread<BigDecimal> cumulativeReadings =
        SpreadUtil.cumulative(BigDecimal.valueOf(70000));

    @BeforeEach
    public void setUp() {
        SpreadUtil.initPackage(
            this,
            this.getClass().getPackage().getName()
        );
    }

    @Test
    public void publicMethodsTest() {

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


}