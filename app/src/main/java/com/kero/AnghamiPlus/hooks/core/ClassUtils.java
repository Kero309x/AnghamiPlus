package com.kero.anghamiplus.hooks.core;

import de.robv.android.xposed.XposedHelpers;

public final class ClassUtils {
    private ClassUtils() {}

    public static Class<?> findClassSafely(String name, ClassLoader loader) {
        try { return XposedHelpers.findClass(name, loader); }
        catch (Throwable t) { Logger.d("Class not found: " + name); return null; }
    }
}