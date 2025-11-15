package com.kero.anghamiplus.hooks.sections;

import com.kero.anghamiplus.hooks.core.BoolHook;
import com.kero.anghamiplus.hooks.core.HookSection;
import com.kero.anghamiplus.hooks.core.Logger;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class PreferenceHelperHooks implements HookSection {

    @Override
    public String getSectionName() {
        return "PreferenceHelper Hooks";
    }

    @Override
    public boolean install(XC_LoadPackage.LoadPackageParam lpparam) {

        boolean ok1 = BoolHook.hook(lpparam,
                "com.anghami.ghost.prefs.PreferenceHelper",
                "getPlusTab",
                false
        );

        boolean ok2 = BoolHook.hook(lpparam,
                "com.anghami.ghost.prefs.PreferenceHelper",
                "getHasRestrictedQueue",
                false
        );

        boolean ok3 = BoolHook.hook(lpparam,
                "com.anghami.ghost.prefs.PreferenceHelper",
                "getAudiobookAdsDisabled",
                true
        );

        boolean ok4 = BoolHook.hook(lpparam,
                "com.anghami.ghost.prefs.PreferenceHelper",
                "getPodcastAdsDisabled",
                true
        );

        int success = (ok1 ? 1 : 0) + (ok2 ? 1 : 0) + (ok3 ? 1 : 0) + (ok4 ? 1 : 0);

        Logger.i("  └─ " + success + "/4 hooks successful");

        return success > 0;
    }
}