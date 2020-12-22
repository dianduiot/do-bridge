package com.github.dianduiot.bridge.model;

public class DoValue extends DoObject {
    public static final Integer VALUE_TYPE_NULL = 0;
    public static final Integer VALUE_TYPE_INT = 1;
    public static final Integer VALUE_TYPE_FLOAT = 2;

    private String propertyId;
    private Integer valueType;
    private Integer valueInt;
    private Float valueFloat;

    public boolean isNull() {
        return valueType == null || valueType.equals(VALUE_TYPE_NULL);
    }

    public float floatValue() {
        if (VALUE_TYPE_NULL.equals(valueType)) {
            return 0.0f;
        } else if (VALUE_TYPE_INT.equals(valueType)) {
            return valueInt;
        } else if (VALUE_TYPE_FLOAT.equals(valueType)) {
            return valueFloat;
        } else {
            return 0.0f;
        }
    }

    public int intValue() {
        if (VALUE_TYPE_NULL.equals(valueType)) {
            return 0;
        } else if (VALUE_TYPE_INT.equals(valueType)) {
            return valueInt;
        } else if (VALUE_TYPE_FLOAT.equals(valueType)) {
            return valueFloat.intValue();
        } else {
            return 0;
        }
    }

    public DoValue() {
        super("V-S");
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public Integer getValueType() {
        return valueType;
    }

    public void setValueType(Integer valueType) {
        this.valueType = valueType;
    }

    public Integer getValueInt() {
        return valueInt;
    }

    public void setValueInt(Integer valueInt) {
        this.valueInt = valueInt;
    }

    public Float getValueFloat() {
        return valueFloat;
    }

    public void setValueFloat(Float valueFloat) {
        this.valueFloat = valueFloat;
    }
}
