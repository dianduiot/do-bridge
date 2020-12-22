package com.github.dianduiot.bridge.model;

public class DoActionResult extends DoObject {
    private String actionId;
    private Integer resultCode;
    private String writeValue;

    public DoActionResult() {
        super("A-R");
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getWriteValue() {
        return writeValue;
    }

    public void setWriteValue(String writeValue) {
        this.writeValue = writeValue;
    }
}
