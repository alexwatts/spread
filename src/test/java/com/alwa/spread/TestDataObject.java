package com.alwa.spread;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestDataObject {

    public BigDecimal publicBigDecimalField;

    private String stringField;
    private Instant timeField;
    private BigDecimal bigDecimalField;
    private BigInteger bigInteger;
    private Integer integerField;
    private Boolean booleanField;
    private List<BigDecimal> listField;
    private Set<Integer> setField;
    private Map<String, Integer> mapField;
    private AnotherTestDataObject nestedObjectField;
    private Map<String, AnotherTestDataObject> nestedObjectMapField;
    private List<AnotherTestDataObject> nestedObjectListField;
    private Set<AnotherTestDataObject> nestedObjectSetField;

    public TestDataObject() {
    }

    public static TestDataObject newInstance(Instant timeField, BigDecimal bigDecimalField) {
        return new TestDataObject(timeField, bigDecimalField);
    }

    public TestDataObject(Instant timeField) {
        this.timeField = timeField;
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

    public Map<String, Integer> getMapField() {
        return mapField;
    }

    public void setMapField(Map<String, Integer> mapField) {
        this.mapField = mapField;
    }

    public Integer getIntegerField() {
        return integerField;
    }

    public void setIntegerField(Integer integerField) {
        this.integerField = integerField;
    }

    public AnotherTestDataObject getNestedObjectField() {
        return nestedObjectField;
    }

    public void setNestedObjectField(AnotherTestDataObject nestedObjectField) {
        this.nestedObjectField = nestedObjectField;
    }

    public Map<String, AnotherTestDataObject> getNestedObjectMapField() {
        return nestedObjectMapField;
    }

    public void setNestedObjectMapField(Map<String, AnotherTestDataObject> nestedObjectMapField) {
        this.nestedObjectMapField = nestedObjectMapField;
    }

    public List<AnotherTestDataObject> getNestedObjectListField() {
        return nestedObjectListField;
    }

    public void setNestedObjectListField(List<AnotherTestDataObject> nestedObjectListField) {
        this.nestedObjectListField = nestedObjectListField;
    }

    public Set<AnotherTestDataObject> getNestedObjectSetField() {
        return nestedObjectSetField;
    }

    public void setNestedObjectSetField(Set<AnotherTestDataObject> nestedObjectSetField) {
        this.nestedObjectSetField = nestedObjectSetField;
    }

    public BigInteger getBigInteger() {
        return bigInteger;
    }
}
