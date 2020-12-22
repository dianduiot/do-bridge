package com.github.dianduiot.vttp;

import com.github.dianduiot.util.ByteUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class VttpFrame {
    private boolean heartbeat = false;
    private boolean encrypted = true;
    private byte[] uri;
    private List<byte[]> params = new ArrayList<>();

    public VttpFrame() {
    }

    public VttpFrame(byte[] uri) {
        this.uri = uri;
    }

    public VttpFrame(String uri) {
        this(uri.getBytes());
    }

    public static VttpFrame buildHeartbeatFrame() {
        VttpFrame frame = new VttpFrame();
        frame.heartbeat = true;
        return frame;
    }

    public VttpBuffer toBuffer() {
        VttpBuffer buffer = new VttpBuffer();
        if (heartbeat) {
            return buffer;
        }
        buffer.appendByte((byte) uri.length);
        buffer.appendBytes(uri, uri.length);
        buffer.appendByte((byte) params.size());
        for (byte[] param : params) {
            if (param == null || param.length == 0) {
                buffer.appendByte((byte) 0);
            } else {
                int paramLength = param.length;
                if (paramLength <= 254) {
                    buffer.appendByte((byte) paramLength);
                } else {
                    buffer.appendByte((byte) 0xFF);
                    buffer.appendByte((byte) (0xFF & paramLength));
                    buffer.appendByte((byte) (0xFF & (paramLength >> 8)));
                }
                buffer.appendBytes(param, paramLength);
            }
        }
        return buffer;
    }

    public static VttpFrame parse(byte[] bytes, int length) {
        VttpFrame frame = new VttpFrame();
        int pos = 0;
        int uriLength = bytes[pos++];
        if (uriLength == 0 || length - pos < uriLength) {
            return null;
        }
        byte[] uri = new byte[uriLength];
        for (int i = 0; i < uriLength; i++) {
            uri[i] = bytes[pos++];
        }
        frame.uri = uri;
        List<byte[]> params = new ArrayList<>();
        if (pos == length) {
            return null;
        }
        int paramCount = bytes[pos++];
        for (int i = 0; i < paramCount; i++) {
            byte[] param = null;
            if (pos == length) {
                return null;
            }
            int paramLength = 0xFF & bytes[pos++];
            if (paramLength == 0xFF) {
                if (length - pos < 2) {
                    return null;
                }
                paramLength = bytes[pos + 1];
                paramLength = paramLength << 8;
                paramLength = paramLength | (0x00FF & bytes[pos]);
                pos += 2;
            }
            if (paramLength > 0) {
                if (length - pos < paramLength) {
                    return null;
                }
                param = new byte[paramLength];
                for (int j = 0; j < paramLength; j++) {
                    param[j] = bytes[pos++];
                }
            }
            params.add(param);
        }
        frame.setParams(params);
        return frame;
    }

    public String toHexStr() {
        VttpBuffer vttpBuffer = this.toBuffer();
        byte[] bytes = vttpBuffer.getBytes();
        int length = vttpBuffer.getLength();
        return ByteUtils.bytesToHexStr(bytes, length);
    }

    public boolean isHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(boolean heartbeat) {
        this.heartbeat = heartbeat;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public byte[] getUri() {
        return uri;
    }

    public void setUri(byte[] uri) {
        this.uri = uri;
    }

    public List<byte[]> getParams() {
        return params;
    }

    public void setParams(List<byte[]> params) {
        this.params = params;
    }

    public void appendEmptyParam() {
        params.add(null);
    }

    public void appendParam(byte[] param) {
        params.add(param);
    }

    public void appendParam(int param) {
        byte[] intParam = new byte[4];
        for (int i = 0; i < 4; i++) {
            intParam[i] = (byte) (0xFF & param);
            param = param >> 8;
        }
        this.appendParam(intParam);
    }

    public void appendParamAsUint(long param) {
        byte[] intParam = new byte[4];
        for (int i = 0; i < 4; i++) {
            intParam[i] = (byte) (0xFF & param);
            param = param >> 8;
        }
        this.appendParam(intParam);
    }

    public void appendParam(float param) {
        this.appendParam(Float.floatToIntBits(param));
    }

    public void appendParam(String param) {
        if (param == null || param.length() == 0) {
            this.appendEmptyParam();
        } else {
            this.appendParam(param.getBytes(StandardCharsets.UTF_8));
        }
    }

    public void appendParamAsByte(int b) {
        byte[] byteParam = new byte[1];
        byteParam[0] = (byte) (b & 0xFF);
        this.appendParam(byteParam);
    }

    public void appendParamAsHexBytes(String hexBytes) {
        int length = 0;
        if (hexBytes != null) {
            length = hexBytes.length() / 2;
        }
        if (length == 0) {
            this.appendEmptyParam();
        } else {
            this.appendParam(ByteUtils.hexStrToBytes(hexBytes));
        }
    }

    public String getUriAsString() {
        return new String(this.uri);
    }

    public byte[] getParam(int index) {
        if (index < this.params.size()) {
            return this.params.get(index);
        } else {
            return null;
        }
    }

    public Integer getParamAsInt(int index) {
        byte[] param = this.getParam(index);
        if (param == null) {
            return null;
        } else {
            int value = 0x00;
            for (int i = 3; i >= 0; i--) {
                value = value << 8;
                value = value | (0x00FF & param[i]);
            }
            return value;
        }
    }


    public Long getParamAsUint(int index) {
        byte[] param = this.getParam(index);
        if (param == null) {
            return null;
        } else {
            long value = 0x00;
            for (int i = 3; i >= 0; i--) {
                value = value << 8;
                value = value | (0x00FF & param[i]);
            }
            return value;
        }
    }

    public Float getParamAsFloat(int index) {
        Integer intValue = this.getParamAsInt(index);
        if (intValue == null) {
            return null;
        } else {
            return Float.intBitsToFloat(intValue);
        }
    }

    public String getParamAsString(int index) {
        byte[] param = this.getParam(index);
        if (param == null) {
            return null;
        } else {
            return new String(param, StandardCharsets.UTF_8);
        }
    }

    public Byte getParamAsByte(int index) {
        byte[] param = this.getParam(index);
        if (param != null && param.length > 0) {
            return param[0];
        } else {
            return null;
        }
    }
}
