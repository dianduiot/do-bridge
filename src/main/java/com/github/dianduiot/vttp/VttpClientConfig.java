package com.github.dianduiot.vttp;

public class VttpClientConfig {
    private String clientName;
    private String deviceId;
    private Integer encryptType;
    private String key;
    private String acceptorHost;
    private int acceptorPort;

    public VttpClientConfig() {
    }

    public VttpClientConfig(String clientName, String deviceId, Integer encryptType, String key, String acceptorHost, int acceptorPort) {
        this.clientName = clientName;
        this.deviceId = deviceId;
        this.encryptType = encryptType;
        this.key = key;
        this.acceptorHost = acceptorHost;
        this.acceptorPort = acceptorPort;
    }

    public VttpClientConfig cloneOne() {
        VttpClientConfig config = new VttpClientConfig();
        config.clientName = this.clientName;
        config.deviceId = this.deviceId;
        config.encryptType = this.encryptType;
        config.key = this.key;
        config.acceptorHost = this.acceptorHost;
        config.acceptorPort = this.acceptorPort;
        return config;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getEncryptType() {
        return encryptType;
    }

    public void setEncryptType(Integer encryptType) {
        this.encryptType = encryptType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAcceptorHost() {
        return acceptorHost;
    }

    public void setAcceptorHost(String acceptorHost) {
        this.acceptorHost = acceptorHost;
    }

    public int getAcceptorPort() {
        return acceptorPort;
    }

    public void setAcceptorPort(int acceptorPort) {
        this.acceptorPort = acceptorPort;
    }
}
