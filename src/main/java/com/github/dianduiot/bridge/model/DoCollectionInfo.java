package com.github.dianduiot.bridge.model;

public class DoCollectionInfo extends DoObject {
    private Integer mode;
    private Integer cycleMs;
    private Integer responseMs;
    private Integer readInterval;
    private Integer writeInterval;
    private Integer maxPropertyCombine;
    private Boolean resetAfterWrote;

    public DoCollectionInfo() {
        super("C-C");
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public Integer getCycleMs() {
        return cycleMs;
    }

    public void setCycleMs(Integer cycleMs) {
        this.cycleMs = cycleMs;
    }

    public Integer getResponseMs() {
        return responseMs;
    }

    public void setResponseMs(Integer responseMs) {
        this.responseMs = responseMs;
    }

    public Integer getReadInterval() {
        return readInterval;
    }

    public void setReadInterval(Integer readInterval) {
        this.readInterval = readInterval;
    }

    public Integer getWriteInterval() {
        return writeInterval;
    }

    public void setWriteInterval(Integer writeInterval) {
        this.writeInterval = writeInterval;
    }

    public Integer getMaxPropertyCombine() {
        return maxPropertyCombine;
    }

    public void setMaxPropertyCombine(Integer maxPropertyCombine) {
        this.maxPropertyCombine = maxPropertyCombine;
    }

    public Boolean getResetAfterWrote() {
        return resetAfterWrote;
    }

    public void setResetAfterWrote(Boolean resetAfterWrote) {
        this.resetAfterWrote = resetAfterWrote;
    }
}
