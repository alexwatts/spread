package com.alwa.spread.custom;

import com.alwa.spread.core.Spread;
import com.alwa.spread.SpreadUtil;
import com.alwa.spread.Spreader;
import com.alwa.spread.model.TestDataObject;
import com.alwa.spread.annotations.In;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CustomSpreadTest {

    @In
    private final Spread<String> callRandomString =
        SpreadUtil.custom((String) -> RandomStringUtils.random(7, true, true));

    @BeforeEach
    public void setUp() {
        SpreadUtil.initPackage(
            this,
            this.getClass().getPackage().getName()
        );
    }

    @Test
    public void randomStringTest() {
        List<TestDataObject> dataObjects =
            new Spreader<TestDataObject>()
                .factory(TestDataObject::new)
                .mutator(testDataObject -> testDataObject.setStringField(Spread.in(callRandomString)))
                .steps(24 * 7)
                .spread()
                .collect(Collectors.toList());

        assertThat(dataObjects.size()).isEqualTo(24 * 7);

        dataObjects
            .stream()
            .map(TestDataObject::getStringField)
            .forEach(s -> assertThat(s.length()).isEqualTo(7));
    }

}
