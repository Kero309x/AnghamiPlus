package com.kero.anghamiplus.hooks.core;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Represents a hook group/section.
 * Each section must implement:
 *  - getSectionName()
 *  - install()
 *
 * Restart support is deprecated and always false 
 * because the module is now fully realtime (no restart needed).
 */
public interface HookSection {

    /**
     * Display name for logs
     */
    String getSectionName();

    /**
     * Install all hooks belonging to this section.
     *
     * @return true if at least one hook was installed successfully
     */
    boolean install(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable;

    /**
     * Deprecated: Restart is no longer required for any hook.
     */
    @Deprecated
    default boolean requiresRestart() {
        return false; // Always false
    }
}