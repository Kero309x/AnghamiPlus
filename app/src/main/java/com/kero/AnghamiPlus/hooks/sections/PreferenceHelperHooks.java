package com.kero.anghamiplus.hooks.sections;

import com.kero.anghamiplus.hooks.core.BoolHook;
import com.kero.anghamiplus.hooks.core.HookSection;
import com.kero.anghamiplus.hooks.core.Logger;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class PreferenceHelperHooks implements HookSection {

    @Override
    public String getSectionName() {
        return "PreferenceHelper Hooks";
    }

    @Override
    public boolean install(XC_LoadPackage.LoadPackageParam lp) {

        Logger.i("  ┌─ Installing PreferenceHelper hooks…");

        int total = 0;
        int success = 0;

        // ───────────────────────────────────────────────
        // Bool hooks list
        // ───────────────────────────────────────────────
        String[][] hooks = {
                {"com.anghami.ghost.prefs.PreferenceHelper", "getPlusTab", "false"},
                {"com.anghami.ghost.prefs.PreferenceHelper", "getHasRestrictedQueue", "false"},
                {"com.anghami.ghost.prefs.PreferenceHelper", "getAudiobookAdsDisabled", "true"},
                {"com.anghami.ghost.prefs.PreferenceHelper", "getPodcastAdsDisabled", "true"}
        };

        for (String[] m : hooks) {
            total++;

            boolean value = m[2].equals("true");
            boolean ok = BoolHook.hook(lp, m[0], m[1], value);

            if (ok) {
                Logger.i("  │   ✓ Hooked: " + m[0] + "." + m[1]);
                success++;
            } else {
                Logger.i("  │   ✗ Failed to hook: " + m[0] + "." + m[1]);
            }
        }

        // ───────────────────────────────────────────────
        // Hook getLibraryConfiguration() → return ""
        // ───────────────────────────────────────────────
        total++;
        try {
            XposedHelpers.findAndHookMethod(
                    "com.anghami.ghost.prefs.PreferenceHelper",
                    lp.classLoader,
                    "getLibraryConfiguration",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(""); // empty string → fallback default links
                        }
                    }
            );

            Logger.i("  │   ✓ Hooked: getLibraryConfiguration → \"\"");
            success++;

        } catch (Throwable t) {
            Logger.i("  │   ✗ Failed to hook getLibraryConfiguration: " + t.getMessage());
        }

        // ───────────────────────────────────────────────
        // Summary
        // ───────────────────────────────────────────────
        Logger.i("  └─ Hooked " + success + "/" + total + " PreferenceHelper methods.");

        return success > 0;
    }
}