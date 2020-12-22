package com.github.dianduiot.bridge.model;

public class DoActionInfo extends DoObject {
    private Integer index;

    private Boolean enableFlag;
    private String actionId;
    private String actionName;
    private Integer inputType;
    private Integer operatorType;
    private String valueStr;
    private String bfpr;
    private String bitMask;
    private Integer sort;

    private byte[] collectionBytes;

    public DoActionInfo() {
        super("C-A-I");
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Boolean getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(Boolean enableFlag) {
        this.enableFlag = enableFlag;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public Integer getInputType() {
        return inputType;
    }

    public void setInputType(Integer inputType) {
        this.inputType = inputType;
    }

    public Integer getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(Integer operatorType) {
        this.operatorType = operatorType;
    }

    public String getValueStr() {
        return valueStr;
    }

    public void setValueStr(String valueStr) {
        this.valueStr = valueStr;
    }

    public String getBfpr() {
        return bfpr;
    }

    public void setBfpr(String bfpr) {
        this.bfpr = bfpr;
    }

    public String getBitMask() {
        return bitMask;
    }

    public void setBitMask(String bitMask) {
        this.bitMask = bitMask;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public byte[] getCollectionBytes() {
        return collectionBytes;
    }

    public void setCollectionBytes(byte[] collectionBytes) {
        this.collectionBytes = collectionBytes;
    }
}
