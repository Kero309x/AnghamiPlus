package com.kero.anghamiplus.hooks.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class HookManager {

    private HookManager() {}

    // Prevent duplicate hook install in same process
    private static final AtomicBoolean installedInThisProcess = new AtomicBoolean(false);

    public static void runAll(XC_LoadPackage.LoadPackageParam lpparam) {

        if (!installedInThisProcess.compareAndSet(false, true)) {
            return; // Already installed in this process
        }

        Logger.i("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        Logger.i("🚀 Installing Hooks for: " + Config.TARGET_PACKAGE);
        Logger.i("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        long startAll = System.currentTimeMillis();

        List<HookSection> sections = new ArrayList<>();

        // 🔥 IMPORTANT — Ensure StartupToastHook loads first
        sections.add(new com.kero.anghamiplus.hooks.sections.StartupDialogHook());

        if (Config.ENABLE_ACCOUNT_HOOKS)
            sections.add(new com.kero.anghamiplus.hooks.sections.AccountHooks());

        if (Config.ENABLE_AD_HOOKS)
            sections.add(new com.kero.anghamiplus.hooks.sections.AdHooks());

        if (Config.ENABLE_KARAOKE_HOOKS)
            sections.add(new com.kero.anghamiplus.hooks.sections.KaraokeHooks());

        if (Config.ENABLE_SHUFFLE_HOOKS)
            sections.add(new com.kero.anghamiplus.hooks.sections.ShuffleHooks());

        if (Config.ENABLE_HEADERBAR_HOOK)
            sections.add(new com.kero.anghamiplus.hooks.sections.HeaderBarHook());

        if (Config.ENABLE_PREF_HELPER_HOOKS)
            sections.add(new com.kero.anghamiplus.hooks.sections.PreferenceHelperHooks());

        if (Config.ENABLE_PLAYQUEUE_HOOKS)
            sections.add(new com.kero.anghamiplus.hooks.sections.PlayQueueHooks());

        if (Config.ENABLE_NETWORK_BLOCK_HOOKS)
            sections.add(new com.kero.anghamiplus.hooks.sections.NetworkBlockHooks());

        if (Config.ENABLE_SESSION_HOOKS)
            sections.add(new com.kero.anghamiplus.hooks.sections.SessionHooks());

        if (Config.ENABLE_DOWNLOAD_HOOKS)
            sections.add(new com.kero.anghamiplus.hooks.sections.DownloadHooks());


        int okCount = 0;

        for (HookSection s : sections) {

            String name = s.getSectionName();
            Logger.i("📦 Loading section: " + name);

            try {
                long tStart = System.currentTimeMillis();

                boolean ok = s.install(lpparam);
                if (ok) okCount++;

                long elapsed = System.currentTimeMillis() - tStart;
                Logger.d("  ⏱ Took " + elapsed + "ms");

            } catch (Throwable t) {
                Logger.e("❌ Error in section: " + name + " (" + t.getClass().getSimpleName() + ")", t);
            }
        }

        long totalMs = System.currentTimeMillis() - startAll;

        Logger.i("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        Logger.i("✅ Hooks Installed: " + okCount + "/" + sections.size());
        Logger.i("⏱ Total time: " + totalMs + "ms");
        Logger.i("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // THIS TOAST NOW WORKS — because StartupToastHook initialized the Application
        ToastUtils.showOnce("🎵 " + Config.MODULE_NAME + " Activated");
    }
}