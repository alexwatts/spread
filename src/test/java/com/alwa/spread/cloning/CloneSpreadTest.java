package com.alwa.spread.cloning;

import com.alwa.spread.SpreadUtil;
import com.alwa.spread.Spreader;
import com.alwa.spread.annotations.In;
import com.alwa.spread.core.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CloneSpreadTest {

    //TODO unwind this when refactoring of ComplexSpread + injectors done
    @In
    private final Spread<String> fixedString = SpreadUtil.fixed("a");

    //TODO unwind this when refactoring of ComplexSpread + injectors done
    @BeforeEach
    public void setUp() {
        SpreadUtil.initPackage(
            this,
            this.getClass().getPackage().getName()
        );
    }

    @Test
    public void testFixedSpreadCanBeCloned() {
        Spread<String> spread = SpreadUtil.fixed("a");
        spread.init(12);

        FixedSpread<String> clonedSpread = (FixedSpread<String>)spread.clone();

        assertThat(clonedSpread.getValues()).isNull();
    }

    @Test
    public void testCustomSpreadCanBeCloned() {
        Spread<Integer> spread = SpreadUtil.custom(() -> Integer.valueOf(5));
        spread.init(12);

        CustomSpread<String> clonedSpread = (CustomSpread<String>)spread.clone();

        assertThat(clonedSpread.getValues()).isNull();
    }

    @Test
    public void testComplexSpreadCanBeCloned() {

        Spread<String> spread = SpreadUtil.complexType(
            new Spreader<String>()
                .factory(() -> String.valueOf(Spread.in(fixedString)))
        );
        spread.init(12);

        ComplexSpread<String> clonedSpread = (ComplexSpread<String>)spread.clone();

        assertThat(clonedSpread.getValues()).isNull();
    }

    @Test
    public void testCumulativeSpreadCanBeCloned() {

        Spread<BigDecimal> spread = SpreadUtil.cumulative(BigDecimal.valueOf(1));
        spread.init(12);

        CumulativeSpread<String> clonedSpread = (CumulativeSpread<String>)spread.clone();

        assertThat(clonedSpread.getValues()).isNull();
    }

    @Test
    public void testRelatedSpreadCanBeCloned() {

        Spread<String> sequenceStrings = SpreadUtil.sequence("a", "b", "c");

        Spread<Boolean> spread = SpreadUtil.
            related(sequenceStrings)
            .step(relatedValue -> relatedValue.startsWith("a"));

        spread.init(12);

        RelatedSpread<String> clonedSpread = (RelatedSpread<String>)spread.clone();

        assertThat(clonedSpread.getValues()).isNull();
    }

    @Test
    public void testSequenceSpreadCanBeCloned() {

        Spread<String> spread = SpreadUtil.sequence("a", "b", "c");

        spread.init(12);

        SequenceSpread<String> clonedSpread = (SequenceSpread<String>)spread.clone();

        assertThat(clonedSpread.getValues()).isNull();
    }

}
