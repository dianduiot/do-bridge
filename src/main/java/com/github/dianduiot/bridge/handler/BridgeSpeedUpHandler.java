package com.github.dianduiot.bridge.handler;

import com.github.dianduiot.bridge.DoHardware;
import com.github.dianduiot.vttp.VttpFrame;

public abstract class BridgeSpeedUpHandler {
    private char quickChar;

    BridgeSpeedUpHandler(char quickChar) {
        this.quickChar = quickChar;
    }

    public void handleSpeedUpFrame(DoHardware hardware, VttpFrame frame) {
        // Fetch sub uri for next component.
        String oriUrl = frame.getUriAsString();
        this.handleFrame(hardware, frame, oriUrl.substring(2));
    }

    public String wrapSubUrl(String subUrl) {
        return "/" + quickChar + subUrl;
    }

    public abstract void handleFrame(DoHardware hardware, VttpFrame frame, String subUrl);
}
