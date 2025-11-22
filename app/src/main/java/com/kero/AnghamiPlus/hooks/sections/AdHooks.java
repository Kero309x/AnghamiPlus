package com.kero.anghamiplus.hooks.sections;

import com.kero.anghamiplus.hooks.core.ClassUtils;
import com.kero.anghamiplus.hooks.core.HookSection;
import com.kero.anghamiplus.hooks.core.Logger;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
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

        Class<?> adSettingsCls = ClassUtils.findClassSafely(
                "com.anghami.ghost.objectbox.models.ads.AdSettings",
                lpparam.classLoader
        );

        Class<?> songCls = ClassUtils.findClassSafely(
                "com.anghami.ghost.pojo.Song",
                lpparam.classLoader
        );

        if (adSettingsCls == null || songCls == null) {
            Logger.i("  └─ ⚠️ AdSettings or Song not found");
            return false;
        }

        Logger.i("  ┌─ Installing Ad hooks…");

        // -------------------------
        // 1) Disable ad-checks (return true)
        // -------------------------
        try {
            XposedHelpers.findAndHookMethod(
                    adSettingsCls,
                    "isAudiobookAdsDisabled",
                    songCls,
                    XC_MethodReplacement.returnConstant(true)
            );
            Logger.i("  │   ✓ Hooked: com.anghami.ghost.objectbox.models.ads.AdSettings.isAudiobookAdsDisabled");
            hooked++;
        } catch (Throwable t) {
            Logger.e("  │   ✗ Failed: isAudiobookAdsDisabled", t);
        }

        try {
            XposedHelpers.findAndHookMethod(
                    adSettingsCls,
                    "isPodcastAdsDisabled",
                    songCls,
                    XC_MethodReplacement.returnConstant(true)
            );
            Logger.i("  │   ✓ Hooked: com.anghami.ghost.objectbox.models.ads.AdSettings.isPodcastAdsDisabled");
            hooked++;
        } catch (Throwable t) {
            Logger.e("  │   ✗ Failed: isPodcastAdsDisabled", t);
        }

        try {
            XposedHelpers.findAndHookMethod(
                    adSettingsCls,
                    "noAd",
                    songCls,
                    XC_MethodReplacement.returnConstant(true)
            );
            Logger.i("  │   ✓ Hooked: com.anghami.ghost.objectbox.models.ads.AdSettings.noAd");
            hooked++;
        } catch (Throwable t) {
            Logger.e("  │   ✗ Failed: noAd", t);
        }

        // -------------------------
        // 2) Prevent actual Ad view from being added to layouts (UI-block)
        //    — we block addView(...) when the view instance is an AdView class
        // -------------------------
        try {
            // try common ad view classes (future-proof)
            Class<?> adViewCls = ClassUtils.findClassSafely(
                    "com.google.android.gms.ads.admanager.AdManagerAdView",
                    lpparam.classLoader
            );
            if (adViewCls == null) {
                adViewCls = ClassUtils.findClassSafely(
                        "com.google.android.gms.ads.BaseAdView",
                        lpparam.classLoader
                );
            }

            // If we couldn't find ad view classes via app classloader, try bootstrap
            if (adViewCls == null) {
                try {
                    adViewCls = Class.forName("com.google.android.gms.ads.admanager.AdManagerAdView");
                } catch (Throwable ignored) {}
                if (adViewCls == null) {
                    try {
                        adViewCls = Class.forName("com.google.android.gms.ads.BaseAdView");
                    } catch (Throwable ignored) {}
                }
            }

            if (adViewCls != null) {
                final Class<?> finalAdViewCls = adViewCls;

                XC_MethodHook blockAdAddHook = new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        // first argument is the View to add
                        if (param.args != null && param.args.length > 0) {
                            Object view = param.args[0];
                            if (view != null && finalAdViewCls.isInstance(view)) {
                                // block adding this ad view to the layout
                                Logger.i("  │   ✓ Blocked Ad view being added to layout");
                                // prevent original addView call
                                param.setResult(null);
                            }
                        }
                    }
                };

                // Hook all addView overloads on android.view.ViewGroup
                try {
                    Class<?> viewGroupCls = Class.forName("android.view.ViewGroup");
                    XposedBridge.hookAllMethods(viewGroupCls, "addView", blockAdAddHook);
                    Logger.i("  │   ✓ Hooked: ViewGroup.addView (ad-block)");
                    hooked++;
                } catch (Throwable t) {
                    Logger.e("  │   ✗ Failed to hook ViewGroup.addView", t);
                }
            } else {
                Logger.i("  │   ⚠️ AdView class not found — fallback UI blocking may not work");
            }
        } catch (Throwable t) {
            Logger.e("  │   ✗ Error while setting up UI blocker", t);
        }

        // -------------------------
        // 3) Keep popup logic intact but avoid adding view if they call getContentView/addView directly
        //    (we already blocked addView; hooking onAdLoaded is optional — we skip setResult here)
        // -------------------------
        try {
            // Optional: just add a light hook to onAdLoaded to log and not break flow
            Class<?> popupCls = ClassUtils.findClassSafely("com.anghami.ui.popupwindow.A", lpparam.classLoader);
            if (popupCls != null) {
                XposedHelpers.findAndHookMethod(
                        popupCls,
                        "onAdLoaded",
                        new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                // just log (no UI actions), main blocking done via addView hook
                                Logger.d("  │   (onAdLoaded) invoked");
                            }
                        }
                );
                // not incrementing hooked here — it's informational only
            }
        } catch (Throwable ignored) {}

        // -------------------------
        // 4) Fake Analytics: intercept Analytics.postEvent(...) and replace ShowFlyerAd events
        // -------------------------
        try {
            Class<?> analyticsCls = ClassUtils.findClassSafely(
                    "com.anghami.ghost.analytics.Analytics",
                    lpparam.classLoader
            );

            Class<?> analyticsEventCls = ClassUtils.findClassSafely(
                    "com.anghami.ghost.analytics.Events$AnalyticsEvent",
                    lpparam.classLoader
            );

            if (analyticsCls != null && analyticsEventCls != null) {
                XposedHelpers.findAndHookMethod(
                        analyticsCls,
                        "postEvent",
                        analyticsEventCls,
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                try {
                                    Object event = param.args[0];
                                    if (event == null) return;

                                    String name = event.getClass().getName();
                                    if (name.contains("ShowFlyerAd")) {
                                        Logger.i("  │   ✓ Faking FlyerAd analytics event");

                                        // Try to build a minimal fake event using builder if available
                                        Class<?> builderCls = ClassUtils.findClassSafely(
                                                "com.anghami.ghost.analytics.Events$Ads$ShowFlyerAd$Builder",
                                                lpparam.classLoader
                                        );

                                        if (builderCls != null) {
                                            try {
                                                Object fakeBuilder = builderCls.newInstance();
                                                Object fakeEvent = XposedHelpers.callMethod(fakeBuilder, "build");
                                                if (fakeEvent != null) param.args[0] = fakeEvent;
                                            } catch (Throwable be) {
                                                // fallback: not critical
                                            }
                                        } else {
                                            // As a fallback, attempt to keep original event but strip ad-specific fields
                                            // (best-effort; avoid throwing)
                                            // nothing to do here if builder not present
                                        }
                                    }
                                } catch (Throwable inner) {
                                    // swallow to avoid breaking analytics
                                }
                            }
                        }
                );
                Logger.i("  │   ✓ Hooked: Analytics.postEvent (flyer ad fake)");
                hooked++;
            } else {
                Logger.i("  │   ⚠️ Analytics classes not found — analytics fake skipped");
            }
        } catch (Throwable t) {
            Logger.e("  │   ✗ Failed analytics hook", t);
        }

        // -------------------------
        Logger.i("  └─ Total hooks: " + hooked);
        return hooked > 0;
    }
}