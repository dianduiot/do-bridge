package com.github.dianduiot.vttp;

public class VttpBuffer {
    public static final byte NO_ENCRYPTED_BEGIN_OR_END_BYTE = (byte) 0x1D;
    public static final byte ENCRYPTED_BEGIN_OR_END_BYTE = (byte) 0x1E;
    public static final byte ESCAPE_CONTROL_BYTE = (byte) 0x10;

    private static final int SLICE_BYTE_SIZE = 64;

    private byte[] bytes;
    private int length;
    private int maxLength;
    private boolean hookControl;
    private boolean encrypted;

    public VttpBuffer() {
        bytes = new byte[SLICE_BYTE_SIZE];
        length = 0;
        maxLength = SLICE_BYTE_SIZE;
        hookControl = false;
        encrypted = false;
    }

    public boolean acceptByte(byte b) {
        if (this.hookControl) {
            this.appendByte(b);
            this.hookControl = false;
        } else if (b == ESCAPE_CONTROL_BYTE) {
            this.hookControl = true;
        } else if (b == NO_ENCRYPTED_BEGIN_OR_END_BYTE || b == ENCRYPTED_BEGIN_OR_END_BYTE) {
            if (this.checkChecksumIsPass()) {
                this.encrypted = b == ENCRYPTED_BEGIN_OR_END_BYTE;
                this.length--;
                return true;
            }
            this.reset();
        } else {
            this.appendByte(b);
        }
        return false;
    }

    private boolean checkChecksumIsPass() {
        if (this.length <= 0) {
            return false;
        }
        byte checksum = (byte) 0x00;
        for (int i = 0; i < this.length - 1; i++) {
            checksum += this.bytes[i];
        }
        return checksum == this.bytes[length - 1];
    }

    public byte computeChecksum() {
        byte checksum = (byte) 0x00;
        for (int i = 0; i < this.length; i++) {
            checksum += this.bytes[i];
        }
        return checksum;
    }

    public void appendByte(byte b) {
        if (length >= maxLength) {
            if (length <= 0) {
                this.expendMaxLengthTo(SLICE_BYTE_SIZE);
            } else {
                this.expendMaxLengthTo(length * 2);
            }
        }
        this.bytes[this.length++] = b;
    }

    public void appendBytes(byte[] data, int length) {
        for (int i = 0; i < length; i++) {
            this.appendByte(data[i]);
        }
    }

    public byte[] toLengthFixBytes() {
        byte[] resultBytes = new byte[this.length];
        System.arraycopy(this.bytes, 0, resultBytes, 0, this.length);
        return resultBytes;
    }

    private void expendMaxLengthTo(int maxLength) {
        if (maxLength > this.maxLength) {
            byte[] newBytes = new byte[maxLength];
            if (this.length > 0) {
                System.arraycopy(this.bytes, 0, newBytes, 0, this.length);
            }
            this.bytes = newBytes;
            this.maxLength = maxLength;
        }
    }

    public void reset() {
        if (this.maxLength != SLICE_BYTE_SIZE) {
            this.bytes = new byte[SLICE_BYTE_SIZE];
            this.maxLength = SLICE_BYTE_SIZE;
        }
        this.length = 0;
        this.hookControl = false;
        this.encrypted = false;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public int getLength() {
        return length;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public boolean isHookControl() {
        return hookControl;
    }

    public boolean isEncrypted() {
        return encrypted;
    }
}
