package com.kero.anghamiplus.hooks.sections;

import com.kero.anghamiplus.hooks.core.ClassUtils;
import com.kero.anghamiplus.hooks.core.HookSection;
import com.kero.anghamiplus.hooks.core.Logger;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class HeaderBarHook implements HookSection {

    @Override
    public String getSectionName() {
        return "HeaderBar Hider";
    }

    @Override
    public boolean install(XC_LoadPackage.LoadPackageParam lp) {

        Logger.i("  ┌─ Installing HeaderBar hooks…");

        // BlueBarItem class is the data passed to HeaderBar
        Class<?> blueBarItem = ClassUtils.findClassSafely(
                "com.anghami.ghost.objectbox.models.BlueBarItem",
                lp.classLoader
        );

        if (blueBarItem == null) {
            Logger.i("  │   ⚠️ BlueBarItem not found — UI may differ in this version");
            return false;
        }

        boolean ok = false;

        // Hook #1 – block setData(BlueBarItem)
        try {
            XposedHelpers.findAndHookMethod(
                    "com.anghami.ui.bar.HeaderBar",
                    lp.classLoader,
                    "setData",
                    blueBarItem,
                    XC_MethodReplacement.returnConstant(null)
            );

            Logger.i("  │   ✓ Hooked: HeaderBar.setData");
            ok = true;

        } catch (Throwable t) {
            Logger.d("  │   (debug) Failed setData: " + t.getClass().getSimpleName());
        }

        // Hook #2 – optional fallback: hideHeader() if exists
        try {
            XposedHelpers.findAndHookMethod(
                    "com.anghami.ui.bar.HeaderBar",
                    lp.classLoader,
                    "showHeader",
                    XC_MethodReplacement.returnConstant(null)
            );

            Logger.i("  │   ✓ Hooked: HeaderBar.showHeader → hidden");

        } catch (Throwable ignore) {
            // UI changes between versions; skip silently
        }

        Logger.i("  └─ HeaderBar hooks: " + (ok ? "active" : "none"));
        return ok;
    }
}