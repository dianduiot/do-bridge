package com.github.dianduiot.vttp;

public class VttpServerConfig {
    private int port;
    private int minThread;
    private int maxThread;
    private int idleTime;
    private int bufferSize;

    public VttpServerConfig() {
    }

    public VttpServerConfig(int port, int minThread, int maxThread, int idleTime, int bufferSize) {
        this.port = port;
        this.minThread = minThread;
        this.maxThread = maxThread;
        this.idleTime = idleTime;
        this.bufferSize = bufferSize;
    }

    public VttpServerConfig cloneOne() {
        VttpServerConfig config = new VttpServerConfig();
        config.port = this.port;
        config.minThread = this.minThread;
        config.maxThread = this.maxThread;
        config.idleTime = this.idleTime;
        config.bufferSize = this.bufferSize;
        return config;
    }

    public static VttpServerConfig genDefault() {
        VttpServerConfig config = new VttpServerConfig();
        config.port = 6665;
        config.minThread = 2;
        config.maxThread = 8;
        config.idleTime = 8;
        config.bufferSize = 4096;
        return config;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMinThread() {
        return minThread;
    }

    public void setMinThread(int minThread) {
        this.minThread = minThread;
    }

    public int getMaxThread() {
        return maxThread;
    }

    public void setMaxThread(int maxThread) {
        this.maxThread = maxThread;
    }

    public int getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(int idleTime) {
        this.idleTime = idleTime;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }
}
