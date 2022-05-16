package com.alwa.spread.sequence;

import com.alwa.spread.core.Spread;
import com.alwa.spread.SpreadUtil;
import com.alwa.spread.Spreader;
import com.alwa.spread.annotations.Dynamic;
import com.alwa.spread.annotations.Embed;
import com.alwa.spread.annotations.In;
import com.alwa.spread.model.TestDataObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DynamicSequenceSpreadTest {

    @In
    private final Spread<Integer> INTEGERS_TOTALING =
        SpreadUtil.sequence(
            SpreadUtil.cumulative(5),
            SpreadUtil.cumulative(10),
            SpreadUtil.cumulative(15),
            SpreadUtil.cumulative(20),
            SpreadUtil.cumulative(25)
        );
    ;

    @In
    @Dynamic
    @Embed(clazz = List.class, steps = 2)
    private final Spread<TestDataObject> DATA_OBJECTS =
        SpreadUtil.complexType(
            new Spreader<TestDataObject>()
                .factory(TestDataObject::new)
                .mutator(testDataObject -> testDataObject.setIntegerField(Spread.in(INTEGERS_TOTALING, 2)))
        );

    @BeforeEach
    public void setup() {
        SpreadUtil.initPackage(
            this,
            this.getClass().getPackage().getName()
        );

    }

    @Test
    public void testDynamicSpreadOfSequences() {
        List<List<TestDataObject>> TEST_DATA_OBJECTS =
            new Spreader<List<TestDataObject>>()
                .factory(ArrayList::new)
                .mutator(list -> list.addAll(Spread.embed(DATA_OBJECTS)))
                .steps(5)
                .spread()
                .collect(
                    Collectors.toList()
                );
        assertThat(
            TEST_DATA_OBJECTS
                .stream().flatMap(List::stream)
                .map(TestDataObject::getIntegerField)
                .reduce(0, Integer::sum)
        ).isEqualTo(Integer.valueOf(75));
    }


}
