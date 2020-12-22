package com.github.dianduiot.vttp;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class VttpEncoder implements ProtocolEncoder {
    @Override
    public void encode(IoSession session, Object o, ProtocolEncoderOutput out) throws Exception {
        VttpFrame frame = (VttpFrame) o;
        if (frame.isHeartbeat()) {
            IoBuffer heartbeatIoBuffer = IoBuffer.allocate(8, false);
            heartbeatIoBuffer.put(VttpBuffer.NO_ENCRYPTED_BEGIN_OR_END_BYTE);
            heartbeatIoBuffer.put((byte) 0x00);
            heartbeatIoBuffer.put(VttpBuffer.NO_ENCRYPTED_BEGIN_OR_END_BYTE);
            heartbeatIoBuffer.flip();
            out.write(heartbeatIoBuffer);
        } else {
            VttpBuffer buffer = frame.toBuffer();
            byte[] resultBytes = buffer.toLengthFixBytes();
            byte beginOrEndByte = VttpBuffer.NO_ENCRYPTED_BEGIN_OR_END_BYTE;
            VttpEncrypt vttpEncrypt = (VttpEncrypt) session.getAttribute(VttpEncrypt.ENCRYPT_ATTR_KEY);
            if (vttpEncrypt != null && vttpEncrypt.isEnable() && frame.isEncrypted()) {
                resultBytes = vttpEncrypt.doEncrypt(resultBytes, resultBytes.length);
                beginOrEndByte = VttpBuffer.ENCRYPTED_BEGIN_OR_END_BYTE;
            }
            if (resultBytes != null && resultBytes.length > 0) {
                IoBuffer ioBuffer = IoBuffer.allocate(resultBytes.length * 2 + 4, false);
                ioBuffer.put(beginOrEndByte);
                byte checksum = (byte) 0x00;
                for (byte b : resultBytes) {
                    if (b == VttpBuffer.NO_ENCRYPTED_BEGIN_OR_END_BYTE || b == VttpBuffer.ENCRYPTED_BEGIN_OR_END_BYTE || b == VttpBuffer.ESCAPE_CONTROL_BYTE) {
                        ioBuffer.put(VttpBuffer.ESCAPE_CONTROL_BYTE);
                    }
                    ioBuffer.put(b);
                    checksum += b;
                }
                if (checksum == VttpBuffer.NO_ENCRYPTED_BEGIN_OR_END_BYTE || checksum == VttpBuffer.ENCRYPTED_BEGIN_OR_END_BYTE || checksum == VttpBuffer.ESCAPE_CONTROL_BYTE) {
                    ioBuffer.put(VttpBuffer.ESCAPE_CONTROL_BYTE);
                }
                ioBuffer.put(checksum);
                ioBuffer.put(beginOrEndByte);
                ioBuffer.flip();
                out.write(ioBuffer);
            }
        }
    }

    @Override
    public void dispose(IoSession ioSession) throws Exception {
    }
}
