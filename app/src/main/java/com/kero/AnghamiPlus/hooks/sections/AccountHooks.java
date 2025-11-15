package com.kero.anghamiplus.hooks.sections;

import com.kero.anghamiplus.hooks.core.BoolHook;
import com.kero.anghamiplus.hooks.core.HookSection;
import com.kero.anghamiplus.hooks.core.Logger;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class AccountHooks implements HookSection {
    @Override public String getSectionName() { return "Account Premium"; }
    @Override public boolean requiresRestart() { return true; }

    @Override
    public boolean install(XC_LoadPackage.LoadPackageParam lpparam) {
        String[][] returnFalse = new String[][]{
                {"com.anghami.ghost.local.Account", "isShowKaraoke"},
                {"com.anghami.ghost.local.Account", "isShowKaraokeUpsellButton"},
                {"com.anghami.ghost.local.Account", "shouldShowKaraokePaywall"},
                {"com.anghami.ghost.model.proto.ProtoAccount$Account", "getEnablePlayerRestrictions"},
                {"com.anghami.ghost.prefs.PreferenceHelper", "getHasRestrictedQueue"}
        };
        String[][] returnTrue = new String[][]{
                {"com.anghami.ghost.local.Account", "isGoldUser"},
                {"com.anghami.ghost.local.Account", "isPlusUser"},
                {"com.anghami.ghost.model.proto.ProtoAccount$Account", "getCanGoLive"}
        };
        int total = returnFalse.length + returnTrue.length;
        int hit = 0;
        for (String[] m : returnFalse) if (BoolHook.hook(lpparam, m[0], m[1], false)) hit++;
        for (String[] m : returnTrue)  if (BoolHook.hook(lpparam, m[0], m[1], true )) hit++;
        Logger.i("  └─ " + hit + "/" + total + " hooks successful");
        return hit > 0;
    }
}