package com.payneteasy.jetty.util;

public class Strings {

    private Strings() {
    }

    public static boolean isEmpty(String aText) {
        return aText == null || aText.length() == 0 || aText.trim().length() == 0;
    }

    public static boolean hasText(String aText) {
        return !isEmpty(aText);
    }

    public static String padRight(String aText, int aLength, char aPadChar) {
        StringBuilder sb = new StringBuilder(aLength);
        sb.append(aText);
        while(sb.length() < aLength) {
            sb.append(aPadChar);
        }
        return sb.toString();
    }
}
