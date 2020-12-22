package com.github.dianduiot.bridge.model;

public class DoTrigger extends DoObject {
    public static final Integer TOUCH_MODE_ARISED = 1;
    public static final Integer TOUCH_MODE_CANCELED = 2;
    public static final Integer TOUCH_MODE_HOLDING = 3;

    private String propertyId;
    private String triggerId;
    private Integer touchMode;
    private Float value;

    public DoTrigger() {
        super("T-T");
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(String triggerId) {
        this.triggerId = triggerId;
    }

    public Integer getTouchMode() {
        return touchMode;
    }

    public void setTouchMode(Integer touchMode) {
        this.touchMode = touchMode;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }
}
