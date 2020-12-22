package com.github.dianduiot.util;

public class ByteUtils {
    public static boolean isSameBytes(byte[] bytes1, byte[] bytes2) {
        if (bytes1 == null || bytes2 == null) {
            return false;
        } else {
            if (bytes1.length != bytes2.length) {
                return false;
            } else {
                for (int i = 0; i < bytes1.length; i++) {
                    if (bytes1[i] != bytes2[i]) {
                        return false;
                    }
                }
                return true;
            }
        }
    }

    public static boolean isBeginWith(byte[] fullBytes, byte[] beginBytes) {
        if (fullBytes == null || beginBytes == null) {
            return false;
        } else {
            if (beginBytes.length > fullBytes.length) {
                return false;
            } else {
                for (int i = 0; i < beginBytes.length; i++) {
                    if (fullBytes[i] != beginBytes[i]) {
                        return false;
                    }
                }
                return true;
            }
        }
    }

    public static byte[] hexStrToBytes(String hexString) {
        if (hexString == null) {
            return null;
        }
        int m, n;
        int byteLen = hexString.length() / 2;
        byte[] ret = new byte[byteLen];
        for (int i = 0; i < byteLen; i++) {
            m = i * 2 + 1;
            n = m + 1;
            int intVal = Integer.decode("0x" + hexString.substring(i * 2, m) + hexString.substring(m, n));
            ret[i] = (byte) intVal;
        }
        return ret;
    }

    public static String bytesToHexStr(byte[] bytes, int length) {
        if (bytes == null) {
            return null;
        }
        String strHex;
        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < length; n++) {
            strHex = Integer.toHexString(bytes[n] & 0xFF);
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex);
        }
        return sb.toString().trim();
    }

    public static String byteToHexStr(int b) {
        String strHex = Integer.toHexString(b & 0xFF);
        return (strHex.length() == 1) ? "0" + strHex : strHex;
    }

    public static int bytes4ToInt(byte[] bytes) {
        if (bytes == null || bytes.length < 4) {
            return 0;
        }
        int value = 0x00;
        for (int i = 3; i >= 0; i--) {
            value = value << 8;
            value = value | bytes[i];
        }
        return value;
    }

    public static long bytes4ToUnsignedInt(byte[] bytes) {
        if (bytes == null || bytes.length < 4) {
            return 0;
        }
        long value = 0x00;
        for (int i = 3; i >= 0; i--) {
            value = value << 8;
            value = value | bytes[i];
        }
        return value;
    }

    public static float bytes4ToFloat(byte[] bytes) {
        if (bytes == null || bytes.length < 4) {
            return 0;
        }
        int value = 0x00;
        for (int i = 3; i >= 0; i--) {
            value = value << 8;
            value = value | bytes[i];
        }
        return Float.intBitsToFloat(value);
    }
}
