package com.kero.anghamiplus.hooks;

import android.os.Handler;
import android.os.Looper;

import com.kero.anghamiplus.hooks.core.Config;
import com.kero.anghamiplus.hooks.core.HookManager;
import com.kero.anghamiplus.hooks.core.Logger;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class Hooker implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals(Config.TARGET_PACKAGE)) return;
        Logger.banner();
        Logger.i("🎯 Target: " + lpparam.packageName + " | Process: " + lpparam.processName);
        new Handler(Looper.getMainLooper()).postDelayed(() -> HookManager.runAll(lpparam), Config.INSTALL_DELAY_MS);
    }
}