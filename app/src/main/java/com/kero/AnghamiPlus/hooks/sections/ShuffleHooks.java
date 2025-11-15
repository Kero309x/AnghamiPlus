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
    public boolean install(XC_LoadPackage.LoadPackageParam lpparam) {

        Class<?> modelClass = ClassUtils.findClassSafely(
                "com.anghami.ghost.pojo.PossiblyGenericModel",
                lpparam.classLoader
        );

        if (modelClass == null) {
            Logger.i("  └─ ⚠️ PossiblyGenericModel not found");
            return false;
        }

        int hooked = 0;
        Logger.i("  ┌─ Installing Shuffle Hooks…");

        // -----------------------------------------------------------
        // 1) getShuffleMode() → false
        // -----------------------------------------------------------
        try {
            XposedHelpers.findAndHookMethod(
                    modelClass,
                    "getShuffleMode",
                    XC_MethodReplacement.returnConstant(false)
            );
            hooked++;
        } catch (Throwable ignored) {}

        // -----------------------------------------------------------
        // 2) setShuffleMode(boolean) → force false
        // -----------------------------------------------------------
        try {
            XposedHelpers.findAndHookMethod(
                    modelClass,
                    "setShuffleMode",
                    boolean.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) {
                            try {
                                XposedHelpers.setBooleanField(param.thisObject, "isShuffleMode", false);
                            } catch (Throwable ignored) {}
                            return null;
                        }
                    }
            );
            hooked++;
        } catch (Throwable ignored) {}

        // -----------------------------------------------------------
        // 3) Freeze field on constructor
        // -----------------------------------------------------------
        try {
            XposedBridge.hookAllConstructors(modelClass, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    try {
                        XposedHelpers.setBooleanField(param.thisObject, "isShuffleMode", false);
                    } catch (Throwable ignored) {}
                }
            });
            hooked++;
        } catch (Throwable ignored) {}

        // -----------------------------------------------------------
        // 4) Strong protection: override field reflection
        // -----------------------------------------------------------
        try {
            Field f = modelClass.getDeclaredField("isShuffleMode");
            f.setAccessible(true);

            Logger.i("  └─ Field 'isShuffleMode' found, applying protection");

            // No need to hook Field.class — not reliable
            // Instead: we enforce the value in all setters

            try {
                XposedHelpers.findAndHookMethod(
                        modelClass,
                        "setIsShuffleMode",
                        boolean.class,
                        XC_MethodReplacement.returnConstant(null)
                );
            } catch (Throwable ignored) {}

        } catch (Throwable ignored) {}


        // ==================================================================
        // 5) Hook getHasShuffleBadge() in header models
        // ==================================================================
        String[] headerClasses = new String[]{
                "com.anghami.model.adapter.headers.AlbumHeaderModel",
                "com.anghami.model.adapter.headers.BaseHeaderModel",
                "com.anghami.model.adapter.headers.PlaylistHeaderModel"
        };

        for (String cls : headerClasses) {
            Class<?> headerClass = ClassUtils.findClassSafely(cls, lpparam.classLoader);

            if (headerClass == null) continue;

            try {
                XposedHelpers.findAndHookMethod(
                        headerClass,
                        "getHasShuffleBadge",
                        XC_MethodReplacement.returnConstant(false)
                );
                hooked++;
            } catch (Throwable ignored) {}
        }

        Logger.i("  └─ Finished: " + hooked + " hooks applied.");
        return hooked > 0;
    }
}
