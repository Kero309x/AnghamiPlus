package com.kero.anghamiplus.hooks.core;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public interface HookSection {
    String getSectionName();
    /** @return true if at least one hook is installed successfully */
    boolean install(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable;
    /** Whether this section typically needs an app restart for full effect */
    default boolean requiresRestart() { return false; }
}