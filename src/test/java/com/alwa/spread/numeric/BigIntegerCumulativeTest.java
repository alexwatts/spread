package com.alwa.spread.numeric;

import com.alwa.spread.core.Spread;
import com.alwa.spread.SpreadUtil;
import com.alwa.spread.Spreader;
import com.alwa.spread.model.TestDataObject;
import com.alwa.spread.annotations.In;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class BigIntegerCumulativeTest {

    @In
    private final Spread<BigInteger> bigIntegerValues =
        SpreadUtil.cumulative(
            BigInteger.valueOf(70000)
        );

    @BeforeEach
    public void setUp() {
        SpreadUtil.initPackage(
            this,
            this.getClass().getPackage().getName()
        );
    }


    @Test
    public void bigIntegerTest() {


        List<TestDataObject> dataObjects =
            new Spreader<TestDataObject>()
                .factory(TestDataObject::new)
                .mutator(
                    testDataObject -> testDataObject.setBigInteger(Spread.in(bigIntegerValues))
                )
                .steps(24 * 7)
                .spread()
                .collect(Collectors.toList());

        assertThat(dataObjects.size()).isEqualTo(24 * 7);

        assertThat(
            dataObjects
                .stream()
                .map(TestDataObject::getBigInteger)
                .reduce(BigInteger.ZERO, BigInteger::add))
            .isEqualTo(BigInteger.valueOf(70000));
    }

}
