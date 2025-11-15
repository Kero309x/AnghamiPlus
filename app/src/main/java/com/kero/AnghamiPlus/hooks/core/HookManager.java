package com.kero.anghamiplus.hooks.core;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class HookManager {
    private HookManager() {}

    private static final AtomicBoolean installed = new AtomicBoolean(false);

    public static void runAll(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!installed.compareAndSet(false, true)) return;

        Logger.i("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        Logger.i("🚀 Starting Hook Installation...");
        Logger.i("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        long totalStart = System.currentTimeMillis();
        HookRegistry registry = new HookRegistry();
        boolean restartRecommended = false;

        List<HookSection> sections = new ArrayList<>();
        if (Config.ENABLE_ACCOUNT_HOOKS) sections.add(new com.kero.anghamiplus.hooks.sections.AccountHooks());
        if (Config.ENABLE_AD_HOOKS) sections.add(new com.kero.anghamiplus.hooks.sections.AdHooks());
        if (Config.ENABLE_KARAOKE_HOOKS) sections.add(new com.kero.anghamiplus.hooks.sections.KaraokeHooks());
        if (Config.ENABLE_SHUFFLE_HOOKS) sections.add(new com.kero.anghamiplus.hooks.sections.ShuffleHooks());
        if (Config.ENABLE_HEADERBAR_HOOK) sections.add(new com.kero.anghamiplus.hooks.sections.HeaderBarHook());
        if (Config.ENABLE_PREF_HELPER_HOOKS) sections.add(new com.kero.anghamiplus.hooks.sections.PreferenceHelperHooks());

        // ✅ NEW: PlayQueue hooks
        if (Config.ENABLE_PLAYQUEUE_HOOKS) sections.add(new com.kero.anghamiplus.hooks.sections.PlayQueueHooks());

        int successSections = 0;
        for (HookSection s : sections) {
            String name = s.getSectionName();
            Logger.i("📦 " + name + "...");
            long start = System.currentTimeMillis();
            boolean ok = false;
            String err = null;
            try {
                ok = s.install(lpparam);
                successSections += ok ? 1 : 0;
            } catch (Throwable t) {
                err = t.getClass().getSimpleName();
                Logger.e("  └─ Error in section: " + name, t);
            }
            long ms = System.currentTimeMillis() - start;
            registry.add(new HookResult(name, ok, ms, err, s.requiresRestart()));
            Logger.d("  ⏱️ Completed in " + ms + "ms");
            if (s.requiresRestart()) restartRecommended = true;
        }

        long total = System.currentTimeMillis() - totalStart;
        Logger.i("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        Logger.i("✅ Installation Complete | Time: " + total + "ms | Sections: " + successSections + "/" + sections.size());
        Logger.i("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        registry.printSummary();

        if (successSections > 0) ToastUtils.showOnce("🎵 " + Config.MODULE_NAME + " Activated ✅");
        if (restartRecommended) new Handler(Looper.getMainLooper()).postDelayed(() ->
                ToastUtils.show("🔄 Restart Anghami for full features!"), 3000);
    }
}