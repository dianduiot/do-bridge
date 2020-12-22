package com.github.dianduiot.util;

public class StringUtils {
    public static boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }

    public static boolean isSameStrWithNull(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        } else if (str2 == null) {
            return false;
        } else {
            return str1.equals(str2);
        }
    }
}
