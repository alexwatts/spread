package com.alwa.spread.validation;

import com.alwa.spread.*;
import com.alwa.spread.core.Spread;
import com.alwa.spread.model.TestDataObject;
import com.alwa.spread.model.PrimativeTestDataObject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidationTest {


    @Test
    public void testUnsupportedCumulativeClassValidation() {
        SpreadException thrown = assertThrows(
            SpreadException.class,
            () -> SpreadUtil.cumulative(new TestDataObject()),
            "Expected SpreadException because cumulative class isn't supported"
        );
        assertThat(thrown.getMessage())
            .contains("Unsupported Cumulative Spread Object - Type:[class com.alwa.spread.model.TestDataObject]");
    }

    @Test
    public void testMissingStepsThrowsValidation() {
        SpreaderException thrown = assertThrows(
            SpreaderException.class,
            () -> new Spreader<PrimativeTestDataObject>()
                .factory(
                    () -> new PrimativeTestDataObject(
                        Spread.in(SpreadUtil.fixed(1)),
                        Spread.in(SpreadUtil.fixed(2d))
                    )
                )
                .spread(),
            "Expected SpreaderException because steps method was missing"
        );
        assertThat(thrown.getMessage())
            .contains("Spreader spread() failure, missing steps. You may need to add a step definition to define how many objects to spread.");
    }

    @Test
    public void testNegativeStepsThrowsValidation() {
        SpreaderException thrown = assertThrows(
            SpreaderException.class,
            () -> new Spreader<PrimativeTestDataObject>()
                .factory(
                    () -> new PrimativeTestDataObject(
                        Spread.in(SpreadUtil.fixed(1)),
                        Spread.in(SpreadUtil.fixed(2d))
                    )
                )
                .steps(-1)
                .spread(),
            "Expected SpreaderException because steps method was missing"
        );
        assertThat(thrown.getMessage())
            .contains("Spreader spread() failure, steps Invalid. Steps must be defined as a positive integer and defines how many objects to spread. Invalid Steps: [-1]");
    }

    @Test
    public void testMissingFactoryThrowsValidation() {
        SpreaderException thrown = assertThrows(
            SpreaderException.class,
            () -> new Spreader<PrimativeTestDataObject>()
                .steps(24 * 7)
                .spread(),
            "Expected SpreaderException because factory was missing"
        );
        assertThat(thrown.getMessage())
            .contains("Spreader spread() failure, missing factory. " +
                "You may need to add a factory to call a constructor, or a factory method, to create instances.");
    }

    @Test
    public void testNegativeCumulativeSpreadThrowsValidation() {
        SpreadException thrown = assertThrows(
            SpreadException.class,
            () -> SpreadUtil.cumulative(BigDecimal.valueOf(-1)),
            "Expected SpreadException because cumulative value is negative"
        );
        assertThat(thrown.getMessage())
            .contains("Invalid Spread Object - Type:[class java.math.BigDecimal], Value:[-1]");
    }

}
