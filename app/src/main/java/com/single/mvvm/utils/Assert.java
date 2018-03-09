package com.single.mvvm.utils;

import com.single.mvvm.BuildConfig;

public class Assert {
    public static void assertTrue(boolean condition, String message) {
        if (BuildConfig.DEBUG) {
            if (!condition) {
                throw new RuntimeException("assert failed: " + message);
            }
        }
    }

    public static void assertTrue(boolean condition) {
        assertTrue(condition, "");
    }

    public static void assertFail() {
        assertTrue(false);
    }
}
