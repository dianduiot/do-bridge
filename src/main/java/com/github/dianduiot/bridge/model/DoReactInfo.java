package com.github.dianduiot.bridge.model;

public class DoReactInfo extends DoObject {
    private Integer index;

    private String reactId;
    private String triggerId;
    private String actionId;
    private Integer triggerTouchMode;
    private Integer sort;

    public DoReactInfo() {
        super("C-R-I");
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getReactId() {
        return reactId;
    }

    public void setReactId(String reactId) {
        this.reactId = reactId;
    }

    public String getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(String triggerId) {
        this.triggerId = triggerId;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public Integer getTriggerTouchMode() {
        return triggerTouchMode;
    }

    public void setTriggerTouchMode(Integer triggerTouchMode) {
        this.triggerTouchMode = triggerTouchMode;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
