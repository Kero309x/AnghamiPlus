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
    public boolean install(XC_LoadPackage.LoadPackageParam lp) {

        Logger.i("  ┌─ Installing PlayQueue hooks…");

        int success = 0;
        int total = 0;

        // ───────────────────────────────────────────────
        // 1) FALSE HOOKS — تعطيل كل القيود
        // ───────────────────────────────────────────────
        String[][] falseMethods = {
                {"com.anghami.odin.playqueue.PlayQueue", "queueRestrictionsEnabled"},
                {"com.anghami.odin.playqueue.PlayQueue", "skipLimitReached", "com.anghami.ghost.local.Account"},
                {"com.anghami.odin.playqueue.PlayQueueManager", "skipLimitReached"},
                {"com.anghami.odin.playqueue.PlayQueue", "shouldForceRelatedMode"},
                {"com.anghami.odin.playqueue.SongPlayqueue", "skipLimitReached"}
        };

        for (String[] m : falseMethods) {
            total++;

            boolean ok;
            if (m.length == 3) {
                ok = BoolHook.hook(lp, m[0], m[1], false, getParamClass(m[2], lp.classLoader));
            } else {
                ok = BoolHook.hook(lp, m[0], m[1], false);
            }

            if (ok) {
                Logger.i("  │   ✓ Hooked: " + m[0] + "." + m[1]);
                success++;
            }
        }

        // ───────────────────────────────────────────────
        // 2) TRUE HOOKS — تفعيل خصائص Plus
        // ───────────────────────────────────────────────
        String[][] trueMethods = {
                // Album PlayQueue
                {"com.anghami.odin.playqueue.AlbumPlayqueue", "getDisablePlayerRestrictions"},
                {"com.anghami.odin.playqueue.AlbumPlayqueue", "getDisableQueueRestrictions"},
                {"com.anghami.odin.playqueue.AlbumPlayqueue", "getDisableSkipLimit"},

                // Playlist PlayQueue
                {"com.anghami.odin.playqueue.PlaylistPlayqueue", "getDisableAds"},
                {"com.anghami.odin.playqueue.PlaylistPlayqueue", "getDisablePlayerRestrictions"},
                {"com.anghami.odin.playqueue.PlaylistPlayqueue", "getDisableQueueRestrictions"},
                {"com.anghami.odin.playqueue.PlaylistPlayqueue", "getDisableSkipLimit"},
                {"com.anghami.odin.playqueue.PlaylistPlayqueue", "getDiscardAds"},

                // Standard PlayQueue
                {"com.anghami.odin.playqueue.PlayQueue", "getDisableAds"},
                {"com.anghami.odin.playqueue.PlayQueue", "getDisablePlayerRestrictions"},
                {"com.anghami.odin.playqueue.PlayQueue", "getDisableQueueRestrictions"},
                {"com.anghami.odin.playqueue.PlayQueue", "getDisableSkipLimit"},
                {"com.anghami.odin.playqueue.PlayQueue", "getDiscardAds"},

                // Manager
                {"com.anghami.odin.playqueue.PlayQueueManager", "playerRestrictionsDisabled"},
                {"com.anghami.odin.playqueue.PlayQueueManager", "adsDisabled"}
        };

        for (String[] m : trueMethods) {
            total++;
            if (BoolHook.hook(lp, m[0], m[1], true)) {
                Logger.i("  │   ✓ Hooked: " + m[0] + "." + m[1]);
                success++;
            }
        }

        // ───────────────────────────────────────────────
        // Summary
        // ───────────────────────────────────────────────
        Logger.i("  └─ Hooked " + success + "/" + total + " PlayQueue methods.");

        return success > 0;
    }


    private Class<?> getParamClass(String name, ClassLoader cl) {
        try { return Class.forName(name, false, cl); }
        catch (Throwable ignored) { return null; }
    }
}