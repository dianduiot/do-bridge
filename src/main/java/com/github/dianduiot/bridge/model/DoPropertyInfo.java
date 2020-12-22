package com.github.dianduiot.bridge.model;

public class DoPropertyInfo extends DoObject {
    private Integer index;

    private Boolean enableFlag;
    private Boolean sendFlag;
    private String propertyId;
    private String propertyName;
    private Integer precision;
    private String afpr;
    private String ignoreValue;
    private Long countDownMs;
    private Long forceUploadMs;
    private Integer sort;

    private byte[] collectionBytes;

    public DoPropertyInfo() {
        super("C-P-I");
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

    public Boolean getSendFlag() {
        return sendFlag;
    }

    public void setSendFlag(Boolean sendFlag) {
        this.sendFlag = sendFlag;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public String getAfpr() {
        return afpr;
    }

    public void setAfpr(String afpr) {
        this.afpr = afpr;
    }

    public String getIgnoreValue() {
        return ignoreValue;
    }

    public void setIgnoreValue(String ignoreValue) {
        this.ignoreValue = ignoreValue;
    }

    public Long getCountDownMs() {
        return countDownMs;
    }

    public void setCountDownMs(Long countDownMs) {
        this.countDownMs = countDownMs;
    }

    public Long getForceUploadMs() {
        return forceUploadMs;
    }

    public void setForceUploadMs(Long forceUploadMs) {
        this.forceUploadMs = forceUploadMs;
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
