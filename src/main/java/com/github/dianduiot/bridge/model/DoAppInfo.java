package com.github.dianduiot.bridge.model;

public class DoAppInfo extends DoObject {
    private String gatewayId;
    private Boolean ddwlLinkEnableFlag;
    private Boolean appLinkEnableFlag;
    private Integer appLinkProtocolType;
    private String appLinkHosts;

    public DoAppInfo() {
        super("C-A");
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public Boolean getDdwlLinkEnableFlag() {
        return ddwlLinkEnableFlag;
    }

    public void setDdwlLinkEnableFlag(Boolean ddwlLinkEnableFlag) {
        this.ddwlLinkEnableFlag = ddwlLinkEnableFlag;
    }

    public Boolean getAppLinkEnableFlag() {
        return appLinkEnableFlag;
    }

    public void setAppLinkEnableFlag(Boolean appLinkEnableFlag) {
        this.appLinkEnableFlag = appLinkEnableFlag;
    }

    public Integer getAppLinkProtocolType() {
        return appLinkProtocolType;
    }

    public void setAppLinkProtocolType(Integer appLinkProtocolType) {
        this.appLinkProtocolType = appLinkProtocolType;
    }

    public String getAppLinkHosts() {
        return appLinkHosts;
    }

    public void setAppLinkHosts(String appLinkHosts) {
        this.appLinkHosts = appLinkHosts;
    }
}
