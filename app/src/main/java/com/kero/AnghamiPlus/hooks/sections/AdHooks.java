package com.kero.anghamiplus.hooks.sections;

import com.kero.anghamiplus.hooks.core.ClassUtils;
import com.kero.anghamiplus.hooks.core.HookSection;
import com.kero.anghamiplus.hooks.core.Logger;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class AdHooks implements HookSection {

    @Override
    public String getSectionName() {
        return "Ad Blocker";
    }

    @Override
    public boolean install(XC_LoadPackage.LoadPackageParam lpparam) {

        int hooked = 0;

                "com.anghami.ghost.objectbox.models.ads.AdSettings",
                lpparam.classLoader
        );

                "com.anghami.ghost.pojo.Song",
                lpparam.classLoader
        );

            Logger.i("  └─ ⚠️ AdSettings or Song not found");
            return false;
        }

        Logger.i("  ┌─ Installing Ad hooks…");

        try {
            XposedHelpers.findAndHookMethod(
                    "isAudiobookAdsDisabled",
                    XC_MethodReplacement.returnConstant(true)
            );
            hooked++;
        } catch (Throwable t) {
        }

        try {
            XposedHelpers.findAndHookMethod(
                    "isPodcastAdsDisabled",
                    XC_MethodReplacement.returnConstant(true)
            );
            hooked++;
        } catch (Throwable t) {
        }

        try {
            XposedHelpers.findAndHookMethod(
                    "noAd",
                    XC_MethodReplacement.returnConstant(true)
            );
            hooked++;
        } catch (Throwable t) {
        }

        try {
                        lpparam.classLoader
                );

                XposedHelpers.findAndHookMethod(
            );
                                        } else {
                                    }
        } catch (Throwable t) {
        }

        Logger.i("  └─ Total hooks: " + hooked);
        return hooked > 0;
    }
}