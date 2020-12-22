package com.github.dianduiot.bridge.model;

public class DoCount extends DoObject {
    private Integer value;

    private Integer protocol;

    public DoCount(String objType) {
        super(objType);
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getProtocol() {
        return protocol;
    }

    public void setProtocol(Integer protocol) {
        this.protocol = protocol;
    }
}
