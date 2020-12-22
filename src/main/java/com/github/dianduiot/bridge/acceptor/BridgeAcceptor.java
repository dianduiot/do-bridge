package com.github.dianduiot.bridge.acceptor;

import com.github.dianduiot.bridge.DoHardware;
import com.github.dianduiot.vttp.VttpFrame;

public class BridgeAcceptor {
    public boolean acceptHandshake(DoHardware hardware) {
        // You can invoke hardware.sendLinkRedirect(host, port) and return false to redirect the link.
        return true;
    }

    public boolean acceptHardware(DoHardware hardware, VttpFrame frame) {
        return true;
    }

    public boolean acceptRoot(DoHardware hardware) {
        return true;
    }
}
