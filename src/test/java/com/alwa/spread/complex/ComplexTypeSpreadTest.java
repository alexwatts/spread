package com.alwa.spread.complex;

import com.alwa.spread.*;
import com.alwa.spread.annotations.In;
import com.alwa.spread.core.Spread;
import com.alwa.spread.model.AnotherTestDataObject;
import com.alwa.spread.model.TestDataObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ComplexTypeSpreadTest {

    @In
    private final Spread<Integer> someIntegers = SpreadUtil.sequence(1, 2, 3, 4, 5, 6, 7 ,8, 9);

    @In
    private final Spread<AnotherTestDataObject> COMPLEX_TYPE_SPREAD =
        SpreadUtil.complexType(
            new Spreader<AnotherTestDataObject>()
                .factory(AnotherTestDataObject::new)
                .mutator(object -> object.setIntField(Spread.in(someIntegers)))
        );

    @BeforeEach
    public void setUp() {
        SpreadUtil.initPackage(
            this,
            this.getClass().getPackage().getName()
        );
    }

    @Test
    public void complexTypeTest() {
        List<TestDataObject> dataObjects =
            new Spreader<TestDataObject>()
                .factory(TestDataObject::new)
                .mutator(testDataObject -> testDataObject.setNestedObjectField(Spread.in(COMPLEX_TYPE_SPREAD)))
                .steps(9)
                .spread()
                .collect(Collectors.toList());

        assertThat(dataObjects.size()).isEqualTo(9);

        assertThat(
            dataObjects
                .stream()
                .map(TestDataObject::getNestedObjectField)
                .map(anotherTestDataObject -> anotherTestDataObject.getIntField())
                .reduce(0, Integer::sum)
        ).isEqualTo(45);

    }

}
