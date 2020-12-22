package com.github.dianduiot.bridge.model;

public class DoConfigItem {

    public static final int TYPE_NONE = 0;
    public static final int TYPE_MASTER_VERSION = 1;
    public static final int TYPE_CLEAR_ALL = 0;
    public static final int TYPE_MAIN_CONFIG = 1;
    public static final int TYPE_REMOVE_PROPERTY = 11;
    public static final int TYPE_SAVE_PROPERTY = 21;
    public static final int TYPE_REMOVE_ACTION = 12;
    public static final int TYPE_SAVE_ACTION = 22;
    public static final int TYPE_REMOVE_TRIGGER = 14;
    public static final int TYPE_SAVE_TRIGGER = 24;

    private int type;
    private String itemId;
    private byte[] syncBytes;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public byte[] getSyncBytes() {
        return syncBytes;
    }

    public void setSyncBytes(byte[] syncBytes) {
        this.syncBytes = syncBytes;
    }
}
