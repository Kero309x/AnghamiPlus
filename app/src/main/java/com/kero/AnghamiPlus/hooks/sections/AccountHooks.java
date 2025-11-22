package com.kero.anghamiplus.hooks.sections;

import com.kero.anghamiplus.hooks.core.BoolHook;
import com.kero.anghamiplus.hooks.core.HookSection;
import com.kero.anghamiplus.hooks.core.Logger;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class AccountHooks implements HookSection {
    @Override public boolean requiresRestart() { return true; }

    @Override
                {"com.anghami.ghost.local.Account", "isShowKaraoke"},
                {"com.anghami.ghost.local.Account", "isShowKaraokeUpsellButton"},
                {"com.anghami.ghost.local.Account", "shouldShowKaraokePaywall"},
                {"com.anghami.ghost.model.proto.ProtoAccount$Account", "getEnablePlayerRestrictions"},
                {"com.anghami.ghost.prefs.PreferenceHelper", "getHasRestrictedQueue"}
        };
                {"com.anghami.ghost.local.Account", "isGoldUser"},
                {"com.anghami.ghost.local.Account", "isPlusUser"},
        };
    }
}