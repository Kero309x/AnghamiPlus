package com.kero.anghamiplus.hooks.sections;

import com.kero.anghamiplus.hooks.core.BoolHook;
import com.kero.anghamiplus.hooks.core.HookSection;
import com.kero.anghamiplus.hooks.core.Logger;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class KaraokeHooks implements HookSection {
    @Override public String getSectionName() { return "Karaoke Unlock"; }
    @Override public boolean requiresRestart() { return true; }

    @Override
    public boolean install(XC_LoadPackage.LoadPackageParam lpparam) {
        String[][] methods = new String[][]{
                {"com.anghami.ghost.local.Account", "showMixAIButtonPlaylist"},
                {"com.anghami.ghost.local.Account", "isCanUseKaraoke"}
        };
        int hit = 0;
        for (String[] m : methods) if (BoolHook.hook(lpparam, m[0], m[1], false)) hit++;
        Logger.i("  └─ " + hit + "/" + methods.length + " hooks successful");
        return hit > 0;
    }
}