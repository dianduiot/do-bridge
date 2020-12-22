package com.github.dianduiot.bridge.model;

public class DoId extends DoObject {
    private String id;

    public DoId(String objType) {
        super(objType);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
