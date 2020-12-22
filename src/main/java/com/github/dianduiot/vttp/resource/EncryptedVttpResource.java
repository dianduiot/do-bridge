package com.github.dianduiot.vttp.resource;

import com.github.dianduiot.vttp.VttpFrame;
import org.apache.mina.core.session.IoSession;

public abstract class EncryptedVttpResource extends VttpResource {
    public EncryptedVttpResource(byte[] uri) {
        super(uri);
    }

    public EncryptedVttpResource(String uri) {
        super(uri);
    }

    @Override
    public void handleFrame(IoSession session, VttpFrame frame) {
        if (frame.isEncrypted()) {
            this.handleEncryptedFrame(session, frame);
        } else {
            session.write(new VttpFrame("/h/eco"));
        }
    }

    public abstract void handleEncryptedFrame(IoSession session, VttpFrame frame);
}
