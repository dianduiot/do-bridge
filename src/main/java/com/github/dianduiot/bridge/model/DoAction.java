package com.github.dianduiot.bridge.model;

public class DoAction extends DoObject {
    private String actionId;
    private String valueStr;

    public DoAction() {
        super("A-P");
    }

    public DoAction(String actionId) {
        this();
        this.actionId = actionId;
    }

    public DoAction(String actionId, String valueStr) {
        this();
        this.actionId = actionId;
        this.valueStr = valueStr;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getValueStr() {
        return valueStr;
    }

    public void setValueStr(String valueStr) {
        this.valueStr = valueStr;
    }
}
