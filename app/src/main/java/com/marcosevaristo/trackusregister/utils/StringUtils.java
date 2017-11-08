package com.marcosevaristo.trackusregister.utils;

public class StringUtils {

    public static boolean isBlank(String texto) {
        return texto == null || "".equals(texto.trim());
    }

    public static boolean isNotBlank(String texto) {
        return !isBlank(texto);
    }

    public static String emptyString() {
        return "";
    }
}
