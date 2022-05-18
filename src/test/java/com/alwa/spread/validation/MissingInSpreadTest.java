package com.alwa.spread.validation;

import com.alwa.spread.SpreadUtil;
import com.alwa.spread.Spreader;
import com.alwa.spread.annotations.In;
import com.alwa.spread.core.Spread;
import com.alwa.spread.exception.SpreadException;
import com.alwa.spread.exception.SpreaderException;
import com.alwa.spread.model.TestDataObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MissingInSpreadTest {

//    @In  (simulate missing @In)
    private final Spread<String> THREE_LETTER_SPREAD =
        SpreadUtil.sequence("a", "b", "c");

    @BeforeEach
    public void setUp() {
        SpreadUtil.initPackage(
            this,
            this.getClass().getPackage().getName()
        );
    }

    @Test
    public void testMissingInYieldsCorrectException() {
        SpreadException thrown = assertThrows(
            SpreadException.class,
            () -> new Spreader<String>()
                    .factory(() -> String.valueOf(Spread.in(THREE_LETTER_SPREAD)))
                    .steps(3)
                    .spread(),
            "Expected SpreaderException because @In annotation was missing."
        );
        assertThat(thrown.getMessage())
            .isEqualTo(String.format(
                "Spread was not initialised before it was used in a Spreader. Spread: [%s] \n" +
                    "- Check the Spread is annotated with @In. \n" +
                    "- Check that the Spread is defined as a field on the Test class passed to initPackage(). \n" +
                    "- Check that the package name passed to initPackage() matches the Test class." , THREE_LETTER_SPREAD));
    }

    @AfterEach
    public void tearDown() {
        SpreadUtil.injectors = null;
    }

}
