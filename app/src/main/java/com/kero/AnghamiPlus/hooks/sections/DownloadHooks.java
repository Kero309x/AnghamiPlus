package com.kero.anghamiplus.hooks.sections;

import com.kero.anghamiplus.hooks.core.ClassUtils;
import com.kero.anghamiplus.hooks.core.HookSection;
import com.kero.anghamiplus.hooks.core.Logger;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class DownloadHooks implements HookSection {

    @Override
    public String getSectionName() {
        return "Download Manager";
    }

    /**
     * No restart required — we inject hooks at load time dynamically.
     */
    @Override
    public boolean requiresRestart() {
        return false;
    }

    @Override
    public boolean install(XC_LoadPackage.LoadPackageParam lpparam) {

        Logger.i("  ┌─ Installing Download Manager hooks…");

        Class<?> dm = ClassUtils.findClassSafely(
                "com.anghami.ghost.downloads.DownloadManager",
                lpparam.classLoader
        );

        if (dm == null) {
            Logger.i("  └─ ⚠️ DownloadManager not found");
            return false;
        }

        int hooked = 0;

        if (hook_assertLimit(dm, lpparam.classLoader)) {
            Logger.i("  │   ✓ Hooked: com.anghami.ghost.downloads.DownloadManager.assertDownloadLimitReached");
            hooked++;
        }

        if (hook_assertRestrictions(dm, lpparam.classLoader)) {
            Logger.i("  │   ✓ Hooked: com.anghami.ghost.downloads.DownloadManager.assertDownloadRestrictions");
            hooked++;
        }

        if (hook_isOnLimitedPlan(dm, lpparam.classLoader)) {
            Logger.i("  │   ✓ Hooked: com.anghami.ghost.downloads.DownloadManager.isOnLimitedPlan");
            hooked++;
        }

        Logger.i("  └─ Total: " + hooked + " hooks installed.");
        return hooked > 0;
    }

    // ───────────────────────────────
    // Internal silent hook methods (use ClassUtils for resilience)
    // ───────────────────────────────

    private boolean hook_assertLimit(Class<?> dm, ClassLoader cl) {
        try {
            Class<?> account = ClassUtils.findClassSafely("com.anghami.ghost.local.Account", cl);
            if (account == null) return false;

            XposedHelpers.findAndHookMethod(
                    dm,
                    "assertDownloadLimitReached",
                    account,
                    int.class,
                    XC_MethodReplacement.returnConstant(null)
            );
            return true;

        } catch (Throwable t) {
            Logger.d("  │   (debug) assertDownloadLimitReached failed: " + t.getClass().getSimpleName());
            return false;
        }
    }

    private boolean hook_assertRestrictions(Class<?> dm, ClassLoader cl) {
        try {
            Class<?> account = ClassUtils.findClassSafely("com.anghami.ghost.local.Account", cl);
            Class<?> reason = ClassUtils.findClassSafely("com.anghami.ghost.objectbox.models.downloads.SongDownloadReason", cl);

            if (account == null || reason == null) return false;

            XposedHelpers.findAndHookMethod(
                    dm,
                    "assertDownloadRestrictions",
                    account,
                    int.class,
                    reason,
                    XC_MethodReplacement.returnConstant(null)
            );
            return true;

        } catch (Throwable t) {
            Logger.d("  │   (debug) assertDownloadRestrictions failed: " + t.getClass().getSimpleName());
            return false;
        }
    }

    private boolean hook_isOnLimitedPlan(Class<?> dm, ClassLoader cl) {
        try {
            Class<?> account = ClassUtils.findClassSafely("com.anghami.ghost.local.Account", cl);
            if (account == null) return false;

            XposedHelpers.findAndHookMethod(
                    dm,
                    "isOnLimitedPlan",
                    account,
                    XC_MethodReplacement.returnConstant(false)
            );
            return true;

        } catch (Throwable t) {
            Logger.d("  │   (debug) isOnLimitedPlan failed: " + t.getClass().getSimpleName());
            return false;
        }
    }
}