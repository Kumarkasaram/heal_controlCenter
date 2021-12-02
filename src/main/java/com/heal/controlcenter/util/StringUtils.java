package com.heal.controlcenter.util;

public class StringUtils {
    public static boolean isEmpty(String s) {
        return (s == null || s.trim().length() == 0);
    }

    public static long getLong(String number) {
        try {
            return Long.parseLong(number);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    public static boolean isNumber(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
