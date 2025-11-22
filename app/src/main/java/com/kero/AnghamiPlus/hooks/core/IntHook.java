package com.kero.anghamiplus.hooks.core;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Integer & String return-value hook system
 * Clean logs, no value-printing, production-friendly.
 */
public final class IntHook {

    private IntHook() {}

    //═══════════════════════════════════════════════════════
    // Hook method returning an int
    //═══════════════════════════════════════════════════════
    public static boolean hook(
            XC_LoadPackage.LoadPackageParam lpparam,
            String className,
            String methodName,
            final int returnValue
    ) {
        try {
            Class<?> cls = XposedHelpers.findClassIfExists(className, lpparam.classLoader);
            if (cls == null) {
                if (Config.DEBUG_MODE)
                    Logger.d("Class not found: " + className);
                return false;
            }

            XposedHelpers.findAndHookMethod(cls, methodName, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam p) {
                    p.setResult(returnValue);
                }
            });

            if (Config.LOG_HOOKS)
                Logger.i("  │   ✓ Hooked: " + className + "." + methodName);

            return true;

        } catch (Throwable t) {
            if (Config.LOG_ERRORS)
                Logger.e("  │   ✗ Failed: " + className + "." + methodName, t);
            return false;
        }
    }

    //═══════════════════════════════════════════════════════
    // Hook method returning a String
    //═══════════════════════════════════════════════════════
    public static boolean hookStringMethod(
            XC_LoadPackage.LoadPackageParam lpparam,
            String className,
            String methodName,
            final String returnValue
    ) {
        try {
            Class<?> cls = XposedHelpers.findClassIfExists(className, lpparam.classLoader);
            if (cls == null) {
                if (Config.DEBUG_MODE)
                    Logger.d("Class not found: " + className);
                return false;
            }

            XposedHelpers.findAndHookMethod(cls, methodName, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam p) {
                    p.setResult(returnValue);
                }
            });

            if (Config.LOG_HOOKS)
                Logger.i("  │   ✓ Hooked: " + className + "." + methodName);

            return true;

        } catch (Throwable t) {
            if (Config.LOG_ERRORS)
                Logger.e("  │   ✗ Failed: " + className + "." + methodName, t);
            return false;
        }
    }
}