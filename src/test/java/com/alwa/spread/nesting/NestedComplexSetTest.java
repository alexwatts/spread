package com.alwa.spread.nesting;

import com.alwa.spread.*;
import com.alwa.spread.annotations.Embed;
import com.alwa.spread.annotations.In;
import com.alwa.spread.model.AnotherTestDataObject;
import com.alwa.spread.model.TestDataObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class NestedComplexSetTest {

    @In
    private final Spread<Integer> CUMULATIVE_INTEGERS = SpreadUtil.cumulative(70000);

    @In
    @Embed(clazz = Set.class, steps = 1000)
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
                    testDataObject.setNestedObjectSetField(
                            (Set<AnotherTestDataObject>)Spread.embed(COMPLEX_TYPE_SPREAD)
                    )
                )
                .steps(1000)
                .spread()
                .collect(Collectors.toList());

        assertThat(dataObjects.size()).isEqualTo(1000);

        assertThat(dataObjects
            .stream()
            .map(TestDataObject::getNestedObjectSetField)
            .flatMap(Collection::stream)
            .map(AnotherTestDataObject::getIntField)
            .reduce(0, Integer::sum))
            .isEqualTo(Integer.valueOf(70000 * 1000));

    }


}
