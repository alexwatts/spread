package com.alwa.spread.primatives;

import com.alwa.spread.model.PrimativeTestDataObject;
import com.alwa.spread.core.Spread;
import com.alwa.spread.SpreadUtil;
import com.alwa.spread.Spreader;
import com.alwa.spread.annotations.In;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PrimativeConstructorTest {

    @In
    private final Spread<Integer> everyInt =
        SpreadUtil
            .initial(1)
            .step(previousInt -> previousInt + 1);

    @In
    private final Spread<Double> someDoubles =
        SpreadUtil
            .initial(0.1d)
            .step(previousDouble -> previousDouble + 1);

    @In
    private final Spread<Double> fixedDouble =
        SpreadUtil
            .fixed(1.6d);

    @In
    private final Spread<Integer> integerValues =
        SpreadUtil.cumulative(
            10000
        );


    @BeforeEach
    public void setUp() {
        SpreadUtil.initPackage(
            this,
            this.getClass().getPackage().getName()
        );
    }

    @Test
    public void primitiveConstructor() {

        List<PrimativeTestDataObject> dataObjects =
            new Spreader<PrimativeTestDataObject>()
                .factory(
                    () -> new PrimativeTestDataObject(
                        Spread.in(everyInt),
                        Spread.in(someDoubles)
                    )
                )
                .steps(24 * 7)
                .spread()
                .collect(Collectors.toList());

        assertThat(dataObjects.size()).isEqualTo(24 * 7);

        dataObjects
            .stream()
            .map(PrimativeTestDataObject::getIntField)
            .forEach(i -> assertThat(i).isGreaterThan(1).isLessThan(170));

        dataObjects
            .stream()
            .map(PrimativeTestDataObject::getDoubleField)
            .forEach(i -> assertThat(i).isGreaterThan(1d).isLessThan(170d));
    }

    @Test
    public void fixedPrimitiveConstructor() {

        List<PrimativeTestDataObject> dataObjects =
            new Spreader<PrimativeTestDataObject>()
                .factory(
                    () -> new PrimativeTestDataObject(
                        Spread.in(everyInt),
                        Spread.in(fixedDouble)
                    )
                )
                .steps(24 * 7)
                .spread()
                .collect(Collectors.toList());

        assertThat(dataObjects.size()).isEqualTo(24 * 7);

        dataObjects
            .stream()
            .map(PrimativeTestDataObject::getIntField)
            .forEach(i -> assertThat(i).isGreaterThan(1).isLessThan(170));

        dataObjects
            .stream()
            .map(PrimativeTestDataObject::getDoubleField)
            .forEach(i -> assertThat(i).isGreaterThan(1d).isLessThan(170d));
    }

    @Test
    public void primativeIntTest() {

        List<PrimativeTestDataObject> dataObjects =
            new Spreader<PrimativeTestDataObject>()
                .factory(
                    () -> new PrimativeTestDataObject(
                        Spread.in(integerValues)
                    )
                )
                .steps(24 * 7)
                .spread()
                .collect(Collectors.toList());

        assertThat(dataObjects.size()).isEqualTo(24 * 7);

        assertThat(
            dataObjects
                .stream()
                .map(PrimativeTestDataObject::getIntField)
                .reduce(0, Integer::sum))
            .isEqualTo(Integer.valueOf(10000));
    }
}
