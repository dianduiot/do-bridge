package com.github.dianduiot.bridge.handler;

import com.github.dianduiot.bridge.DoHardware;
import com.github.dianduiot.util.StringUtils;
import com.github.dianduiot.vttp.VttpFrame;
import org.apache.mina.core.session.IoSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class BridgeTransferHandler extends BridgeSpeedUpHandler {
    // --------------------------------------------------
    // Should override methods BEGIN.
    /** This method will invoke when transfer bridge ready. */
    public void onTransferReady(DoHardware hardware) {
    }

    /** This method will invoke when transfer bridge released and the gateway had been recovery to collection mode, or other mode. */
    public void onTransferReleased(DoHardware hardware) {
    }

    /** This method will invoke when a packet received. */
    public void onPacketReceived(DoHardware hardware, byte[] bytes) {
    }

    // Should override methods END.
    // --------------------------------------------------

    public BridgeTransferHandler() {
        super('t');
    }

    @Override
    public void handleFrame(DoHardware hardware, VttpFrame frame, String subUrl) {
        if ("/p".equals(subUrl)) {
            this.handlePacketReceived(hardware, frame);
        } else if ("/h-ok".equals(subUrl)) {
            this.onTransferReady(hardware);
        } else if ("/r-ok".equals(subUrl)) {
            this.onTransferReleased(hardware);
        }
    }

    protected void handlePacketReceived(DoHardware hardware, VttpFrame frame) {
        this.onPacketReceived(hardware, frame.getParam(0));
    }
}
