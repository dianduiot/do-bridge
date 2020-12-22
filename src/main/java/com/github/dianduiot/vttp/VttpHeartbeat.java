package com.github.dianduiot.vttp;

public class VttpHeartbeat {
    public static final String HEARTBEAT_ATTR_KEY = "HB";

    private long lastReceivedTs;
    private int sendCd;
    private int cycle;

    public long getLastReceivedTs() {
        return lastReceivedTs;
    }

    public void setLastReceivedTs(long lastReceivedTs) {
        this.lastReceivedTs = lastReceivedTs;
    }

    public int getSendCd() {
        return sendCd;
    }

    public void setSendCd(int sendCd) {
        this.sendCd = sendCd;
    }

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }
}
