package com.kero.anghamiplus.hooks.sections;

import com.kero.anghamiplus.hooks.core.ClassUtils;
import com.kero.anghamiplus.hooks.core.Config;
import com.kero.anghamiplus.hooks.core.HookSection;
import com.kero.anghamiplus.hooks.core.Logger;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class NetworkBlockHooks implements HookSection {

    @Override
    public String getSectionName() {
        return "Enhanced Network Blocker";
    }

    // 🔥 كل المسارات الأساسية للإعلانات
    private static final String[] BLOCKED_PATTERNS = {
            "GETadpriorities.view",
            "GETads.view",
            "GETprerollAdsContent.view",
            "GETsubscribeSheet.view",
            "GETsubscribe.view",
            "REGISTERad.view",
            "/get-ad-in-player",
            "GETDisplayAds.view",
            "GETad.view",
            "bls.view",
            "displayAds",
            "adscontent",

            // Google / Doubleclick
            "pubads.g.doubleclick.net",
            "googleads.g.doubleclick.net",
            "pagead2.googlesyndication.com",

            // Anghami Ads CDN
            "anghnewads.anghcdn.co",
            "ads.anghcdn.co",

            // 3rd party ad networks
            "taboola",
            "adcolony",
            "unityads",
            "facebook.com/tr",
            "app-measurement.com",
            "analytics",
    };

    // 🔥 قائمة كلمات محجوبة (Dynamic Match)
    private static final String[] KEYWORDS = {
            "/ad",
            "/ads",
            "adserver",
            "adclick",
            "adunit",
            "adcontent",
            "advert",
            "tracking",
            "impression",
            "viewability",
            "sponsored",
            "campaign",
    };

    @Override
    public boolean install(XC_LoadPackage.LoadPackageParam lp) {

        Logger.i("  ┌─ Installing Enhanced Network Blocker…");

        Class<?> builderClass = ClassUtils.findClassSafely("okhttp3.Request$Builder", lp.classLoader);

        if (builderClass == null) {
            Logger.i("  │   ⚠️ Request.Builder not found — trying fallback hooks…");
            return tryFallback(lp);
        }

        boolean ok = applyBlockHook(builderClass);

        Logger.i("  └─ Network Blocker Status: " + (ok ? "ACTIVE" : "FAILED"));
        return ok;
    }

    private boolean tryFallback(XC_LoadPackage.LoadPackageParam lp) {

        String[] fallback = {
                "okhttp3.internal.http.RequestBuilder",
                "com.anghami.network.OkHttpRequestBuilder",
                "okhttp3.RequestBuilder"
        };

        for (String cls : fallback) {
            Class<?> c = ClassUtils.findClassSafely(cls, lp.classLoader);
            if (c != null) {
                if (applyBlockHook(c)) {
                    Logger.i("  │   ✓ Fallback Hook Activated: " + cls);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean applyBlockHook(Class<?> target) {

        try {

            XposedHelpers.findAndHookMethod(
                    target,
                    "build",
                    new XC_MethodHook() {

                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {

                            try {

                                Object builder = param.thisObject;

                                Object httpUrl = XposedHelpers.getObjectField(builder, "url");
                                if (httpUrl == null) return;

                                String url = httpUrl.toString();

                                // Full matching
                                if (shouldBlock(url)) {

                                    if (Config.DEBUG_MODE) {
                                        Logger.d("[BLOCKED_URL] " + url);
                                    }

                                    // Cancel the request safely
                                    param.setResult(null);

                                }

                            } catch (Throwable ignore) {
                                // Never crash the app
                            }
                        }
                    }
            );

            return true;

        } catch (Throwable ignore) {
            return false;
        }
    }

    // 🔥 أفضل Matching شامل للإعلانات
    private boolean shouldBlock(String url) {

        if (url == null) return false;

        url = url.toLowerCase();

        // Basic patterns
        for (String p : BLOCKED_PATTERNS) {
            if (url.contains(p.toLowerCase())) {
                return true;
            }
        }

        // Dynamic keyword blocking
        for (String k : KEYWORDS) {
            if (url.contains(k)) {
                return true;
            }
        }

        // Generic rule:
        // احظر أي request يبدأ بـ GETad*
        if (url.contains("GETad".toLowerCase())) {
            return true;
        }

        return false;
    }
}