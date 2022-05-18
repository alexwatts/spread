package com.alwa.spread.validation;

import com.alwa.spread.SpreadUtil;
import com.alwa.spread.Spreader;
import com.alwa.spread.annotations.In;
import com.alwa.spread.core.Spread;
import com.alwa.spread.exception.SpreadException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MissingSetupSpreadTest {

    @In
    private final Spread<String> THREE_LETTER_SPREAD =
        SpreadUtil.sequence("a", "b", "c");

    //@BeforeEach
    public void setUp() {
        SpreadUtil.initPackage(
            this,
            this.getClass().getPackage().getName()
        );
    }

    @Test
    public void testMissingSetupUpYieldsCorrectException() {
        SpreadException thrown = assertThrows(
            SpreadException.class,
            () -> new Spreader<String>()
                .factory(() -> String.valueOf(Spread.in(THREE_LETTER_SPREAD)))
                .steps(3)
                .spread(),
            "Expected SpreaderException because test setup was missing."
        );
        assertThat(thrown.getMessage())
            .isEqualTo(String.format(
                "Spread was not initialised before a Spreader was used. \n" +
                    "- Check the test setup was done with a call to SpreadUtil.initPackage(). \n"));
    }

    @AfterEach
    public void tearDown() {
        SpreadUtil.injectors = null;
    }

}