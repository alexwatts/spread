package com.alwa.spread.convenience;

import com.alwa.spread.SpreadUtil;
import com.alwa.spread.Spreader;
import com.alwa.spread.annotations.Dynamic;
import com.alwa.spread.annotations.Embed;
import com.alwa.spread.annotations.In;
import com.alwa.spread.core.Spread;
import com.alwa.spread.model.TestDataObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ConvenienceTest {

    @BeforeEach
    public void setup() {
        SpreadUtil.initPackage(
            this,
            this.getClass().getPackage().getName()
        );
    }

    @In
    private final Spread<String> KEYS = SpreadUtil.sequence("a", "b", "c");

    @In
    private final Spread<BigDecimal> VALUES = SpreadUtil.fixed(BigDecimal.valueOf(10000));

    @In
    @Embed(clazz = List.class, steps = 3)
    private final Spread<BigDecimal> VALUES_EMBEDDED = SpreadUtil.cumulative(BigDecimal.valueOf(10000));

    @In
    @Dynamic
    @Embed(clazz = List.class, steps = 5)
    private final Spread<TestDataObject> DATA_OBJECTS =
        SpreadUtil.complexType(
            new Spreader<TestDataObject>()
                .factory(TestDataObject::new)
                .mutator(testDataObject -> testDataObject.setBigDecimalField(Spread.in(VALUES, 1000)))
        );

    @Test
    public void testConvenienceToMap() {
        Map<String, BigDecimal> MAP = SpreadUtil.toMap(3, KEYS, VALUES);

        assertThat(
            MAP.values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        ).isEqualTo(BigDecimal.valueOf(30000));
    }

    @Test
    public void testConvenienceToEmbeddedMap() {
        Map<String, List<BigDecimal>> MAP =
            SpreadUtil.toEmbeddedMap(3, KEYS, VALUES_EMBEDDED);

        assertThat(
            MAP.values()
                .stream()
                .flatMap(List::stream)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        ).isEqualTo(BigDecimal.valueOf(30000));
    }

    @Test
    public void testConvenienceEmbeddedListSpreadOfSequences() {
        List<List<TestDataObject>> TEST_DATA_OBJECTS =
            SpreadUtil.toEmbeddedList(5, DATA_OBJECTS);

        assertThat(
            TEST_DATA_OBJECTS
                .stream().flatMap(List::stream)
                .map(TestDataObject::getBigDecimalField)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        ).isEqualTo(BigDecimal.valueOf(250000));
    }

}
