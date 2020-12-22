package com.github.dianduiot.vttp;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class VttpEncrypt {
    public static final String ENCRYPT_ATTR_KEY = "VE";

    public static final int ENCRYPT_TYPE_NONE = 0;
    public static final int ENCRYPT_TYPE_AES128_CBC_PKCS5PADDING = 1;
    public static final int ENCRYPT_TYPE_AES192_CBC_PKCS5PADDING = 2;
    public static final int ENCRYPT_TYPE_AES256_CBC_PKCS5PADDING = 3;

    private int encryptType;
    private byte[] key;
    private byte[] iv;
    private boolean enable = false;
    private boolean necHook = false;

    private byte[] usingKey = null;

    public VttpEncrypt(int encryptType, byte[] key, byte[] iv) {
        this.encryptType = encryptType;
        this.key = key;
        this.iv = iv;
    }

    private byte[] cutKey(int length) {
        byte[] resultKey = new byte[length];
        System.arraycopy(this.key, 0, resultKey, 0, length);
        return resultKey;
    }

    public void refreshRealKey() {
        this.usingKey = null;
    }

    public byte[] realKey() {
        if (this.encryptType == ENCRYPT_TYPE_AES128_CBC_PKCS5PADDING) {
            if (this.usingKey == null) {
                this.usingKey = cutKey(16);
            }
            return this.usingKey;
        } else if (this.encryptType == ENCRYPT_TYPE_AES192_CBC_PKCS5PADDING) {
            if (this.usingKey == null) {
                this.usingKey = cutKey(24);
            }
            return this.usingKey;
        } else {
            return this.key;
        }
    }

    public byte[] doEncrypt(byte[] data, int length) {
        try {
            data = this.fixByteLengthAsLength(data, length);
            SecretKeySpec secretKeySpec = new SecretKeySpec(this.realKey(), "AES");
            IvParameterSpec ivParameterSpec = this.buildCurrentAesIvSpec();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            return null;
        }
    }

    public byte[] doDecrypt(byte[] data, int length) {
        try {
            data = this.fixByteLengthAsLength(data, length);
            SecretKeySpec secretKeySpec = new SecretKeySpec(this.realKey(), "AES");
            IvParameterSpec ivParameterSpec = this.buildCurrentAesIvSpec();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            return null;
        }
    }

    private IvParameterSpec buildCurrentAesIvSpec() {
        byte[] processingIv = new byte[iv.length];
        System.arraycopy(iv, 0, processingIv, 0, iv.length);
        return new IvParameterSpec(processingIv);
    }

    private byte[] fixByteLengthAsLength(byte[] bytes, int length) {
        if (length != bytes.length) {
            byte[] newBytes = new byte[length];
            System.arraycopy(bytes, 0, newBytes, 0, length);
            return newBytes;
        } else {
            return bytes;
        }
    }

    public int getEncryptType() {
        return encryptType;
    }

    public void setEncryptType(int encryptType) {
        this.encryptType = encryptType;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isNecHook() {
        return necHook;
    }

    public void setNecHook(boolean necHook) {
        this.necHook = necHook;
    }
}
