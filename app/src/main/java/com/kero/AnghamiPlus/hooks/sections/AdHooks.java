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

        Class<?> adClass = ClassUtils.findClassSafely(
                "com.anghami.ghost.objectbox.models.ads.AdSettings",
                lpparam.classLoader
        );

        Class<?> songClass = ClassUtils.findClassSafely(
                "com.anghami.ghost.pojo.Song",
                lpparam.classLoader
        );

        if (adClass == null || songClass == null) {
            Logger.i("  └─ ⚠️ AdSettings or Song not found");
            return false;
        }

        Logger.i("  ┌─ Installing Ad hooks…");

        // ======================================================
        // 1) private boolean isAudiobookAdsDisabled(Song)
        // ======================================================
        try {
            XposedHelpers.findAndHookMethod(
                    adClass,
                    "isAudiobookAdsDisabled",
                    songClass,
                    XC_MethodReplacement.returnConstant(true)
            );
            Logger.i("  └─ isAudiobookAdsDisabled hooked");
            hooked++;
        } catch (Throwable t) {
            Logger.e("  └─ Failed isAudiobookAdsDisabled", t);
        }

        // ======================================================
        // 2) private boolean isPodcastAdsDisabled(Song)
        // ======================================================
        try {
            XposedHelpers.findAndHookMethod(
                    adClass,
                    "isPodcastAdsDisabled",
                    songClass,
                    XC_MethodReplacement.returnConstant(true)
            );
            Logger.i("  └─ isPodcastAdsDisabled hooked");
            hooked++;
        } catch (Throwable t) {
            Logger.e("  └─ Failed isPodcastAdsDisabled", t);
        }

        // ======================================================
        // 3) public static boolean noAd(Song)
        // ======================================================
        try {
            XposedHelpers.findAndHookMethod(
                    adClass,
                    "noAd",
                    songClass,
                    XC_MethodReplacement.returnConstant(true)
            );
            Logger.i("  └─ noAd hooked");
            hooked++;
        } catch (Throwable t) {
            Logger.e("  └─ Failed noAd", t);
        }

        // ======================================================
        // 4) Disable popup window ads/promos
        // ======================================================
        try {
            // Load popup parameter class
            Class<?> popupParam = ClassUtils.findClassSafely(
                    "com.anghami.ui.popupwindow.a",
                    lpparam.classLoader
            );

            if (popupParam != null) {
                XposedHelpers.findAndHookMethod(
                        "com.anghami.ui.popupwindow.A",
                        lpparam.classLoader,
                        "i",
                        popupParam,
                        XC_MethodReplacement.returnConstant(null)
                );
                Logger.i("  └─ Popup hook applied successfully");
            } else {
                Logger.i("  └─ ⚠️ Popup parameter class not found");
            }

        } catch (Throwable t) {
            Logger.e("  └─ Popup hook failed", t);
        }

        Logger.i("  └─ Total hooks: " + hooked);
        return hooked > 0;
    }
}
