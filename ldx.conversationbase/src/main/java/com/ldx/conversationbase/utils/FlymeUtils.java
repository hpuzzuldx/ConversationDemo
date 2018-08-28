package com.ldx.conversationbase.utils;

import android.os.Build;

import java.lang.reflect.Method;

public final class FlymeUtils {
    public static boolean isFlyme() {
        try {
            final Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (final Exception e) {
            return false;
        }
    }
}