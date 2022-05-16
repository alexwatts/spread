package com.alwa.spread.nesting;

import com.alwa.spread.core.Spread;
import com.alwa.spread.SpreadUtil;
import com.alwa.spread.Spreader;
import com.alwa.spread.model.TestDataObject;
import com.alwa.spread.annotations.Embed;
import com.alwa.spread.annotations.In;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class NestedSetTest {

    @In
    @Embed(clazz = Set.class, steps = 6)
    private final Spread<Integer> cumulativeReadingsListed =
        SpreadUtil.cumulative(70000);

    @BeforeEach
    public void setUp() {
        SpreadUtil.initPackage(
            this,
            this.getClass().getPackage().getName()
        );
    }

    @Test
    public void testNestedSetField() {
        List<TestDataObject> dataObjects =
            new Spreader<TestDataObject>()
                .factory(TestDataObject::new)
                .mutators(testDataObject ->
                    testDataObject.setSetField((Set<Integer>)Spread.embed(cumulativeReadingsListed)))
                .steps(24 * 7)
                .spread()
                .collect(Collectors.toList());

        assertThat(dataObjects.size()).isEqualTo(24 * 7);

        assertThat(dataObjects
            .stream()
            .map(TestDataObject::getSetField)
            .flatMap(Collection::stream)
            .reduce(0, Integer::sum))
            .isEqualTo(Integer.valueOf(3919944));
    }

}
