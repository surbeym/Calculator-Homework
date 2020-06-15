package org.juancampos.utils;

public class VariableExpression {
    private VariableExpression(){};

    public VariableExpression(long value, boolean isValueAssigned){
        this.value = value;
        this.valueAssigned = isValueAssigned;
    }
    public boolean isValueAssigned() {
        return valueAssigned;
    }

    public void setIsValueAssigned(boolean valueAssigned) {
        this.valueAssigned = valueAssigned;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public static VariableExpression of (long value, boolean isValueAssigned){
        return new VariableExpression(value,isValueAssigned);
    }

    boolean valueAssigned = false;
    long value;
}
