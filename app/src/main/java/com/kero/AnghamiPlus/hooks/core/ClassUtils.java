package com.kero.anghamiplus.hooks.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.robv.android.xposed.XposedHelpers;

public final class ClassUtils {

    private ClassUtils() {}

    // Cache to avoid repeated lookups
    private static final Map<String, Class<?>> CACHE = new ConcurrentHashMap<>();

    /**
     * Safely load class with:
     *  ✔ Level 1: direct findClass
     *  ✔ Level 2: Class.forName
     *  ✔ Level 3: automatic inner-class fallback (A.B -> A$B)
     *  ✔ Silent failure (debug-only logs)
     */
    public static Class<?> findClassSafely(String name, ClassLoader loader) {
        if (name == null) return null;

        // Already loaded?
        if (CACHE.containsKey(name))
            return CACHE.get(name);

        Class<?> result = null;

        // LEVEL 1 — XposedHelpers.findClass
        try {
            result = XposedHelpers.findClass(name, loader);
            if (result != null) {
                CACHE.put(name, result);
                return result;
            }
        } catch (Throwable ignored) { }

        // LEVEL 2 — Class.forName
        try {
            result = Class.forName(name, false, loader);
            if (result != null) {
                CACHE.put(name, result);
                return result;
            }
        } catch (Throwable ignored) { }

        // LEVEL 3 — Auto-convert "A.B" → "A$B" (Inner Class)
        if (name.contains(".")) {
            String alt = name.substring(0, name.lastIndexOf('.'))
                    + "$"
                    + name.substring(name.lastIndexOf('.') + 1);

            try {
                result = XposedHelpers.findClass(alt, loader);
                if (result != null) {
                    CACHE.put(name, result); // map original name too
                    return result;
                }
            } catch (Throwable ignored) { }
        }

        // LEVEL 4 — log only if debug mode ON
        Logger.d("Class not found: " + name);
        CACHE.put(name, null);
        return null;
    }
}