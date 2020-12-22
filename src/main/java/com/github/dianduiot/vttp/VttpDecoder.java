package com.github.dianduiot.vttp;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class VttpDecoder implements ProtocolDecoder {
    private final static String RECEIVE_BUFFER_KEY = "RB";

    @Override
    public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        int remaining = in.remaining();
        if (remaining <= 0) {
            return;
        }

        boolean newBufferFlag = false;
        VttpBuffer buffer = (VttpBuffer) session.getAttribute(RECEIVE_BUFFER_KEY);
        if (buffer == null) {
            buffer = new VttpBuffer();
            newBufferFlag = true;
        }

        for (int i = 0; i < remaining; i++) {
            byte b = in.get();
            if (buffer.acceptByte(b)) {
                this.handleSliceReceived(session, buffer, out);
                buffer.reset();
            }
        }

        if (newBufferFlag) {
            session.setAttribute(RECEIVE_BUFFER_KEY, buffer);
        }
    }

    private void handleSliceReceived(IoSession session, VttpBuffer buffer, ProtocolDecoderOutput out) {
        int length = buffer.getLength();
        if (length == 0) {
            out.write(VttpFrame.buildHeartbeatFrame());
        } else {
            byte[] bytes = buffer.getBytes();
            VttpEncrypt encrypt = (VttpEncrypt) session.getAttribute(VttpEncrypt.ENCRYPT_ATTR_KEY);
            if (buffer.isEncrypted()) {
                if (encrypt != null) {
                    bytes = encrypt.doDecrypt(bytes, length);
                    if (bytes == null) {
                        return;
                    } else {
                        length = bytes.length;
                    }
                } else {
                    return;
                }
            }
            VttpFrame vttpFrame = VttpFrame.parse(bytes, length);
            if (vttpFrame != null) {
                if (encrypt != null && encrypt.isNecHook()) {
                    vttpFrame.setEncrypted(true);
                } else {
                    vttpFrame.setEncrypted(buffer.isEncrypted());
                }
                out.write(vttpFrame);
            }
        }
    }

    @Override
    public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
    }

    @Override
    public void dispose(IoSession session) throws Exception {
    }
}
