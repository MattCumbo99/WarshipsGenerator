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

    private StringUtility() {
        throw new IllegalStateException("Cannot instantiate static class.");
    }
}
