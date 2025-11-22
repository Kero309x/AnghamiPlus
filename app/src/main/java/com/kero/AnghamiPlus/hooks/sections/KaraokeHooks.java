package com.kero.anghamiplus.hooks.sections;

import com.kero.anghamiplus.hooks.core.BoolHook;
import com.kero.anghamiplus.hooks.core.HookSection;
import com.kero.anghamiplus.hooks.core.Logger;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class KaraokeHooks implements HookSection {

    @Override
    public String getSectionName() {
        return "Karaoke Unlock";
    }

    @Override
    public boolean requiresRestart() {
        return false; // Karaoke works instantly — no restart needed
    }

    @Override
    public boolean install(XC_LoadPackage.LoadPackageParam lp) {

        Logger.i("  ┌─ Installing Karaoke hooks…");

        String[][] methods = {
                {"com.anghami.ghost.local.Account", "showMixAIButtonPlaylist"},
                {"com.anghami.ghost.local.Account", "isCanUseKaraoke"}
        };

        int success = 0;

        for (String[] m : methods) {
            if (BoolHook.hook(lp, m[0], m[1], false)) {
                Logger.i("  │   ✓ Hooked: " + m[0] + "." + m[1]);
                success++;
            }
        }

        Logger.i("  └─ Hooked " + success + "/" + methods.length + " methods.");

        return success > 0;
    }
}