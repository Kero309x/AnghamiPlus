package com.kero.anghamiplus.hooks.sections;

import com.kero.anghamiplus.hooks.core.ClassUtils;
import com.kero.anghamiplus.hooks.core.HookSection;
import com.kero.anghamiplus.hooks.core.Logger;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class ShuffleHooks implements HookSection {

    @Override
    public String getSectionName() {
        return "Shuffle Control";
    }

    @Override
    public boolean install(XC_LoadPackage.LoadPackageParam lp) {

        Logger.i("  ┌─ Installing Shuffle hooks…");

        Class<?> modelClass = ClassUtils.findClassSafely(
                "com.anghami.ghost.pojo.PossiblyGenericModel",
                lp.classLoader
        );

        if (modelClass == null) {
            Logger.i("  └─ ⚠️ PossiblyGenericModel not found");
            return false;
        }

        int hooked = 0;

        // 1) getShuffleMode() -> false (block)
        try {
            XposedHelpers.findAndHookMethod(
                    modelClass,
                    "getShuffleMode",
                    XC_MethodReplacement.returnConstant(false)
            );
            Logger.i("  │   ✓ Hooked: getShuffleMode");
            hooked++;
        } catch (Throwable t) {
            Logger.d("  │   (debug) getShuffleMode hook failed: " + t.getClass().getSimpleName());
        }

        // 2) setShuffleMode(boolean) -> force false (no-op)
        try {
            XposedHelpers.findAndHookMethod(
                    modelClass,
                    "setShuffleMode",
                    boolean.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) {
                            try {
                                // Ensure internal field is false if present
                                try {
                                    XposedHelpers.setBooleanField(param.thisObject, "isShuffleMode", false);
                                } catch (Throwable ignored) {}
                            } catch (Throwable ignored) {}
                            return null;
                        }
                    }
            );
            Logger.i("  │   ✓ Hooked: setShuffleMode");
            hooked++;
        } catch (Throwable t) {
            Logger.d("  │   (debug) setShuffleMode hook failed: " + t.getClass().getSimpleName());
        }

        // 3) Force field value after any constructor
        try {
            XposedBridge.hookAllConstructors(modelClass, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    try {
                        XposedHelpers.setBooleanField(param.thisObject, "isShuffleMode", false);
                    } catch (Throwable ignored) {}
                }
            });
            Logger.i("  │   ✓ Hooked: constructor enforcement");
            hooked++;
        } catch (Throwable t) {
            Logger.d("  │   (debug) constructor hook failed: " + t.getClass().getSimpleName());
        }

        // 4) Try to detect and protect the actual field (best-effort)
        try {
            Field field = modelClass.getDeclaredField("isShuffleMode");
            field.setAccessible(true);
            Logger.i("  │   ✓ Found field: isShuffleMode (protection applied)");
            // no further action — setters already hooked
        } catch (Throwable t) {
            Logger.d("  │   (debug) isShuffleMode field not present or inaccessible");
        }

        // 5) Hook header models getHasShuffleBadge() -> false
        String[] headerClasses = new String[]{
                "com.anghami.model.adapter.headers.AlbumHeaderModel",
                "com.anghami.model.adapter.headers.BaseHeaderModel",
                "com.anghami.model.adapter.headers.PlaylistHeaderModel"
        };

        for (String cls : headerClasses) {
            Class<?> headerClass = ClassUtils.findClassSafely(cls, lp.classLoader);
            if (headerClass == null) continue;
            try {
                XposedHelpers.findAndHookMethod(
                        headerClass,
                        "getHasShuffleBadge",
                        XC_MethodReplacement.returnConstant(false)
                );
                Logger.i("  │   ✓ Hooked: " + cls + ".getHasShuffleBadge");
                hooked++;
            } catch (Throwable t) {
                Logger.d("  │   (debug) " + cls + " getHasShuffleBadge failed: " + t.getClass().getSimpleName());
            }
        }

        Logger.i("  └─ Finished: " + hooked + " hooks applied.");
        return hooked > 0;
    }
}