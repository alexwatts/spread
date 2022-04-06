package com.alwa.spread;

import java.math.BigDecimal;
import java.time.Instant;

public class TestDataObject {

    private String stringField;
    private Instant timeField;
    private BigDecimal bigDecimalField;

    public TestDataObject() {
    }

    public static TestDataObject newInstance(Instant timeField, BigDecimal bigDecimalField) {
        return new TestDataObject(timeField, bigDecimalField);
    }

    public TestDataObject(Instant timeField, BigDecimal bigDecimalField) {
        this.timeField = timeField;
        this.bigDecimalField = bigDecimalField;
    }

    public TestDataObject factoryMethod(Instant timeField, BigDecimal bigDecimalField) {
        return new TestDataObject(timeField, bigDecimalField);
    }

    public String getStringField() {
        return stringField;
    }

    public void setStringField(String stringField) {
        this.stringField = stringField;
    }

    public Instant getTimeField() {
        return timeField;
    }

    public void setTimeField(Instant timeField) {
        this.timeField = timeField;
    }

    public BigDecimal getBigDecimalField() {
        return bigDecimalField;
    }

    public void setBigDecimalField(BigDecimal bigDecimalField) {
        this.bigDecimalField = bigDecimalField;
    }

}
