package com.alwa.spread;

public class PrimativeTestDataObject {

    private String stringField;
    private int intField;
    private double doubleField;

    public PrimativeTestDataObject() {
    }

    public PrimativeTestDataObject(int intField, double doubleField) {
        this.intField = intField;
        this.doubleField = doubleField;
    }

    public PrimativeTestDataObject factoryMethod(int intField, double doubleField) {
        return new PrimativeTestDataObject(intField, doubleField);
    }

    public String getStringField() {
        return stringField;
    }

    public void setStringField(String stringField) {
        this.stringField = stringField;
    }

    public int getIntField() {
        return intField;
    }

    public void setIntField(int intField) {
        this.intField = intField;
    }

    public double getDoubleField() {
        return doubleField;
    }

    public void setDoubleField(double doubleField) {
        this.doubleField = doubleField;
    }

}
