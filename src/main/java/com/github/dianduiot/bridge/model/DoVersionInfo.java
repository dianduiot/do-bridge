package com.github.dianduiot.bridge.model;

public class DoVersionInfo extends DoObject {
    private String currMasterVersion;
    private String currSlaveVersion;
    private String targetMasterVersion;
    private String targetSlaveVersion;

    public DoVersionInfo() {
        super("C-V");
    }

    public String getCurrMasterVersion() {
        return currMasterVersion;
    }

    public void setCurrMasterVersion(String currMasterVersion) {
        this.currMasterVersion = currMasterVersion;
    }

    public String getCurrSlaveVersion() {
        return currSlaveVersion;
    }

    public void setCurrSlaveVersion(String currSlaveVersion) {
        this.currSlaveVersion = currSlaveVersion;
    }

    public String getTargetMasterVersion() {
        return targetMasterVersion;
    }

    public void setTargetMasterVersion(String targetMasterVersion) {
        this.targetMasterVersion = targetMasterVersion;
    }

    public String getTargetSlaveVersion() {
        return targetSlaveVersion;
    }

    public void setTargetSlaveVersion(String targetSlaveVersion) {
        this.targetSlaveVersion = targetSlaveVersion;
    }
}
