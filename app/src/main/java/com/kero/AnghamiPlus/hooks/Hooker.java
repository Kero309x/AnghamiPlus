package com.kero.anghamiplus.hooks;

import com.kero.anghamiplus.hooks.core.Config;
import com.kero.anghamiplus.hooks.core.HookManager;
import com.kero.anghamiplus.hooks.core.Logger;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class Hooker implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        // Only hook Anghami package
        if (!lpparam.packageName.equals(Config.TARGET_PACKAGE)) return;

        // Optional nicely formatted banner
        Logger.banner();

        Logger.i("🎯 Target: " + lpparam.packageName +
                " | Process: " + lpparam.processName);

        // Run hooks IMMEDIATELY — no delay, no handler
        HookManager.runAll(lpparam);
    }
}