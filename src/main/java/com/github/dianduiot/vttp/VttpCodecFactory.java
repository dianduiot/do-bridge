package com.github.dianduiot.vttp;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class VttpCodecFactory implements ProtocolCodecFactory {
    private final VttpEncoder vttpEncoder;
    private final VttpDecoder vttpDecoder;

    public VttpCodecFactory() {
        this.vttpEncoder = new VttpEncoder();
        this.vttpDecoder = new VttpDecoder();
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        return this.vttpEncoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return this.vttpDecoder;
    }
}
