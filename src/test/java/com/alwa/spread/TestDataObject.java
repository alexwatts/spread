package com.alwa.spread;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.Set;

public class TestDataObject {

    public BigDecimal publicBigDecimalField;

    private String stringField;
    private Instant timeField;
    private BigDecimal bigDecimalField;
    private BigInteger bigInteger;
    private Boolean booleanField;
    private List<BigDecimal> listField;
    private Set<Integer> setField;

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

    public void setBigInteger(BigInteger bigInteger) {
        this.bigInteger = bigInteger;
    }

    public Boolean getBooleanField() {
        return booleanField;
    }

    public void setBooleanField(Boolean booleanField) {
        this.booleanField = booleanField;
    }

    public List<BigDecimal> getListField() {
        return listField;
    }

    public void setListField(List<BigDecimal> listField) {
        this.listField = listField;
    }

    public Set<Integer> getSetField() {
        return setField;
    }

    public void setSetField(Set<Integer> setField) {
        this.setField = setField;
    }
}
