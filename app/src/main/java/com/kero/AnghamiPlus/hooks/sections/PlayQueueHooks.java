package com.kero.anghamiplus.hooks.sections;

import com.kero.anghamiplus.hooks.core.BoolHook;
import com.kero.anghamiplus.hooks.core.HookSection;
import com.kero.anghamiplus.hooks.core.Logger;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class PlayQueueHooks implements HookSection {

    @Override
    public String getSectionName() {
        return "PlayQueue Hooks";
    }

    @Override
    public boolean install(XC_LoadPackage.LoadPackageParam lpparam) {

        int hit = 0;

        // ────────────────────────────────
        // 🔻 FALSE HOOKS (Disable restrictions)
        // ────────────────────────────────
        hit += BoolHook.hook(lpparam,
                "com.anghami.odin.playqueue.PlayQueue",
                "queueRestrictionsEnabled",
                false
        ) ? 1 : 0;

        hit += BoolHook.hook(lpparam,
                "com.anghami.odin.playqueue.PlayQueue",
                "skipLimitReached",
                false,
                "com.anghami.ghost.local.Account"
        ) ? 1 : 0;

        hit += BoolHook.hook(lpparam,
                "com.anghami.odin.playqueue.PlayQueueManager",
                "skipLimitReached",
                false
        ) ? 1 : 0;

        hit += BoolHook.hook(lpparam,
                "com.anghami.odin.playqueue.PlayQueue",
                "shouldForceRelatedMode",
                false
        ) ? 1 : 0;

        hit += BoolHook.hook(lpparam,
                "com.anghami.odin.playqueue.SongPlayqueue",
                "skipLimitReached",
                false
        ) ? 1 : 0;


        // ────────────────────────────────
        // ✅ TRUE HOOKS (Enable plus behaviour)
        // ────────────────────────────────
        String[][] trueMethods = new String[][]{

                // AlbumPlayqueue
                {"com.anghami.odin.playqueue.AlbumPlayqueue", "getDisablePlayerRestrictions"},
                {"com.anghami.odin.playqueue.AlbumPlayqueue", "getDisableQueueRestrictions"},
                {"com.anghami.odin.playqueue.AlbumPlayqueue", "getDisableSkipLimit"},

                // PlaylistPlayqueue
                {"com.anghami.odin.playqueue.PlaylistPlayqueue", "getDisableAds"},
                {"com.anghami.odin.playqueue.PlaylistPlayqueue", "getDisablePlayerRestrictions"},
                {"com.anghami.odin.playqueue.PlaylistPlayqueue", "getDisableQueueRestrictions"},
                {"com.anghami.odin.playqueue.PlaylistPlayqueue", "getDisableSkipLimit"},
                {"com.anghami.odin.playqueue.PlaylistPlayqueue", "getDiscardAds"},

                // PlayQueue
                {"com.anghami.odin.playqueue.PlayQueue", "getDisableAds"},
                {"com.anghami.odin.playqueue.PlayQueue", "getDisablePlayerRestrictions"},
                {"com.anghami.odin.playqueue.PlayQueue", "getDisableQueueRestrictions"},
                {"com.anghami.odin.playqueue.PlayQueue", "getDisableSkipLimit"},
                {"com.anghami.odin.playqueue.PlayQueue", "getDiscardAds"},

                // PlayQueueManager
                {"com.anghami.odin.playqueue.PlayQueueManager", "playerRestrictionsDisabled"},
                {"com.anghami.odin.playqueue.PlayQueueManager", "adsDisabled"},
        };

        for (String[] m : trueMethods) {
            hit += BoolHook.hook(lpparam, m[0], m[1], true) ? 1 : 0;
        }

        Logger.i("  └─ " + hit + "/20 hooks successful");
        return hit > 0;
    }
}