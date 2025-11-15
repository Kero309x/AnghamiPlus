package com.kero.anghamiplus.hooks.core;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class BoolHook {

    private BoolHook() {}

    /**
     * Hook method returning boolean (no params)
     */
    public static boolean hook(XC_LoadPackage.LoadPackageParam lpparam,
                               String className,
                               String methodName,
                               boolean returnValue) {
        return hookInternal(lpparam, className, methodName, returnValue);
    }

    /**
     * Hook method returning boolean (with params)
     */
    public static boolean hook(XC_LoadPackage.LoadPackageParam lpparam,
                               String className,
                               String methodName,
                               boolean returnValue,
                               Object... params) {
        return hookInternal(lpparam, className, methodName, returnValue, params);
    }

    private static boolean hookInternal(XC_LoadPackage.LoadPackageParam lpparam,
                                        String className,
                                        String methodName,
                                        boolean returnValue,
                                        Object... params) {
        try {
            Object[] methodArgs = concat(params, XC_MethodReplacement.returnConstant(returnValue));
            XposedHelpers.findAndHookMethod(className, lpparam.classLoader, methodName, methodArgs);
            Logger.d("  ✓ " + methodName + " → " + returnValue);
            return true;
        } catch (Throwable t) {
            Logger.d("  ✗ " + methodName + " (" + t.getClass().getSimpleName() + ")");
            return false;
        }
    }

    private static Object[] concat(Object[] arr, Object last) {
        Object[] out = new Object[arr.length + 1];
        System.arraycopy(arr, 0, out, 0, arr.length);
        out[arr.length] = last;
        return out;
    }
}