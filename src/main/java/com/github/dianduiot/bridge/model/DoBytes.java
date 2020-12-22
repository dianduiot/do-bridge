package com.github.dianduiot.bridge.model;

public class DoBytes extends DoObject {
    private String itemId;
    private int bytesType;
    private byte[] bytes;

    public DoBytes() {
        super("C-B");
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getBytesType() {
        return bytesType;
    }

    public void setBytesType(int bytesType) {
        this.bytesType = bytesType;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
