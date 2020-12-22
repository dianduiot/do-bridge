package com.github.dianduiot.vttp.resource;

import com.github.dianduiot.util.ByteUtils;
import com.github.dianduiot.vttp.VttpFrame;
import org.apache.mina.core.session.IoSession;

public abstract class VttpResource {
    private final byte[] uri;

    protected boolean prefixMatch = false;

    public VttpResource(byte[] uri) {
        this.uri = uri;
    }

    public VttpResource(String uri) {
        this.uri = uri.getBytes();
    }

    public boolean isMatchUri(byte[] uri) {
        if (this.prefixMatch) {
            return ByteUtils.isBeginWith(uri, this.uri);
        } else {
            return ByteUtils.isSameBytes(this.uri, uri);
        }
    }

    public abstract void handleFrame(IoSession session, VttpFrame frame);
}
