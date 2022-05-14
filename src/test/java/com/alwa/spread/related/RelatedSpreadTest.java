package com.alwa.spread.related;

import com.alwa.spread.*;
import com.alwa.spread.annotations.In;
import com.alwa.spread.model.TestDataObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RelatedSpreadTest {

    @In
    private final Spread<String> threeLetterSpread =
        SpreadUtil.sequence("a", "b", "c");

    @In
    private final Spread<Boolean> startsWithAnA =
        SpreadUtil.
            related(threeLetterSpread)
            .step(relatedValue -> relatedValue.startsWith("a"));

    @BeforeEach
    public void setUp() {
        SpreadUtil.initPackage(
            this,
            this.getClass().getPackage().getName()
        );
    }

    @Test
    public void testBasedOn() {
        List<TestDataObject> dataObjects =
            new Spreader<TestDataObject>()
                .factory(TestDataObject::new)
                .mutator(
                    testDataObject -> testDataObject.setBooleanField(Spread.in(startsWithAnA))
                )
                .steps(24 * 7)
                .spread()
                .collect(Collectors.toList());

        assertThat(dataObjects.size()).isEqualTo(24 * 7);

        IntStream.range(0, dataObjects.size())
            .forEach(
                i ->
                    assertThat(dataObjects.get(i).getBooleanField()).isEqualTo(shouldBeAnA(i + 1))
            );
    }

    private boolean shouldBeAnA(int step) {
        return step % 3 == 1;
    }

}
