package com.kero.anghamiplus.hooks.sections;

import com.kero.anghamiplus.hooks.core.ClassUtils;
import com.kero.anghamiplus.hooks.core.HookSection;
import com.kero.anghamiplus.hooks.core.Logger;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class HeaderBarHook implements HookSection {
    @Override public String getSectionName() { return "HeaderBar Hider"; }

    @Override
    public boolean install(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            Class<?> blueBarItem = ClassUtils.findClassSafely("com.anghami.ghost.objectbox.models.BlueBarItem", lpparam.classLoader);
            if (blueBarItem == null) { Logger.i("  └─ ⚠️ BlueBarItem class not found"); return false; }

            XposedHelpers.findAndHookMethod("com.anghami.ui.bar.HeaderBar", lpparam.classLoader, "setData", blueBarItem, XC_MethodReplacement.returnConstant(null));
            Logger.i("  └─ ✓ HeaderBar blocked successfully");
            return true;
        } catch (Throwable t) {
            Logger.i("  └─ ⚠️ Failed: " + t.getMessage());
            return false;
        }
    }
}