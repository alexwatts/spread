package com.alwa.spread.nesting;

import com.alwa.spread.Spread;
import com.alwa.spread.SpreadUtil;
import com.alwa.spread.Spreader;
import com.alwa.spread.model.TestDataObject;
import com.alwa.spread.annotations.Embed;
import com.alwa.spread.annotations.In;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class NestedListTest {

    @In
    @Embed(clazz = List.class, steps = 7)
    private final Spread<BigDecimal> cumulativeReadingsListed =
        SpreadUtil.cumulative(BigDecimal.valueOf(70000));

    @BeforeEach
    public void setUp() {
        SpreadUtil.initPackage(
            this,
            this.getClass().getPackage().getName()
        );
    }

    @Test
    public void testNestedListField() {
        List<TestDataObject> dataObjects =
            new Spreader<TestDataObject>()
                .factory(TestDataObject::new)
                .mutators(testDataObject ->
                    testDataObject.setListField(
                        (List<BigDecimal>)Spread.embed(cumulativeReadingsListed)
                    )
                )
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

}
