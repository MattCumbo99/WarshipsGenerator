package com.warships.utils;

public final class StringUtility {

    public static String repeat(String str, int amount) {
        String result = "";
        while (amount > 0) {
            result += str;
            amount--;
        }

        return result;
    }

    public static String repeat(Character ch, int amount) {
        return repeat(String.valueOf(ch), amount);
    }

    public static boolean isNotBlank(String string) {
        return string != null && !string.isBlank();
    }

    private StringUtility() {
        throw new IllegalStateException("Cannot instantiate static class.");
    }
}
