package com.alwa.spread.nesting;

import com.alwa.spread.*;
import com.alwa.spread.annotations.Embed;
import com.alwa.spread.annotations.In;
import com.alwa.spread.core.Spread;
import com.alwa.spread.model.AnotherTestDataObject;
import com.alwa.spread.model.TestDataObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class NestedComplexMapTest {

    @In
    private final Spread<Integer> CUMULATIVE_INTEGERS = SpreadUtil.cumulative(70000);

    @In
    private final Spread<String> MAP_KEYS_SPREAD =
        SpreadUtil.custom((String) -> RandomStringUtils.random(7, true, true));

    @In
    @Embed(clazz = Map.class, steps = 1000)
    private final Spread<AnotherTestDataObject> COMPLEX_TYPE_SPREAD =
        SpreadUtil.complexType(
            new Spreader<AnotherTestDataObject>()
                .factory(AnotherTestDataObject::new)
                .mutator(object -> object.setIntField(Spread.in(CUMULATIVE_INTEGERS)))
        );

    @BeforeEach
    public void setUp() {
        SpreadUtil.initPackage(
            this,
            this.getClass().getPackage().getName()
        );
    }

    @Test
    public void testNestedMapWithSpreader() {

        List<TestDataObject> dataObjects =
            new Spreader<TestDataObject>()
                .factory(TestDataObject::new)
                .mutators(testDataObject ->
                    testDataObject.setNestedObjectMapField(
                        Spread.embedMap(COMPLEX_TYPE_SPREAD, MAP_KEYS_SPREAD)
                    )
                )
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


}
