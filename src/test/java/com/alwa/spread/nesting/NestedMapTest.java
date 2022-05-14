package com.alwa.spread.nesting;

import com.alwa.spread.Spread;
import com.alwa.spread.SpreadUtil;
import com.alwa.spread.Spreader;
import com.alwa.spread.model.TestDataObject;
import com.alwa.spread.annotations.Embed;
import com.alwa.spread.annotations.In;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class NestedMapTest {

    @In
    @Embed(clazz = Map.class, steps = 6)
    private final Spread<Integer> cumulativeReadingsListed =
        SpreadUtil.cumulative(70000);

    @In
    private final Spread<String> randomMapKey =
        SpreadUtil.custom((String) -> RandomStringUtils.random(7, true, true));

    @BeforeEach
    public void setUp() {
        SpreadUtil.initPackage(
            this,
            this.getClass().getPackage().getName()
        );
    }

    @Test
    public void testNestedMapField() {

        List<TestDataObject> dataObjects =
            new Spreader<TestDataObject>()
                .factory(TestDataObject::new)
                .mutators(testDataObject ->
                    testDataObject.setMapField(
                        Spread.embedMap(cumulativeReadingsListed, randomMapKey)
                    )
                )
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

}
