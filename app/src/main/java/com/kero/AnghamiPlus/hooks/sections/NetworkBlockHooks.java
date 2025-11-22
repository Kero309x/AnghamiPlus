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
        return "Network Blocker";
    }

    private static final String[] BLOCKED_PATTERNS = {
            "GETadpriorities.view",
            "GETads.view",
            "GETprerollAdsContent.view",
            "GETsubscribeSheet.view",
            "GETsubscribe.view",
            "REGISTERad.view",
            "pubads.g.doubleclick.net",
            "googleads.g.doubleclick.net"
    };

    @Override
    public boolean install(XC_LoadPackage.LoadPackageParam lp) {

        Logger.i("  ┌─ Installing Network Blocker…");

        // Primary OkHttp builder class
        Class<?> builder = ClassUtils.findClassSafely("okhttp3.Request$Builder", lp.classLoader);

        if (builder == null) {
            Logger.i("  │   ⚠️ Request.Builder not found — trying alternatives…");
            return tryFallback(lp);
        }

        boolean ok = applyBlockHook(builder);

        Logger.i("  └─ Network Blocker: " + (ok ? "active" : "no hooks"));
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
                boolean ok = applyBlockHook(c);
                if (ok) {
                    Logger.i("  │   ✓ Hooked via fallback: " + cls);
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

                                // extract URL
                                Object httpUrl = XposedHelpers.getObjectField(builder, "url");
                                if (httpUrl == null) return;

                                String url = httpUrl.toString();

                                // match block rules
                                if (matches(url)) {

                                    // debug only — user will not see it
                                    if (Config.DEBUG_MODE) {
                                        Logger.d("[BLOCKED] " + url);
                                    }

                                    param.setResult(null);  // silently cancel request
                                }

                            } catch (Throwable ignore) {
                                // Never break the app — silent fail
                            }
                        }
                    }
            );

            return true;

        } catch (Throwable ignore) {
            return false;
        }
    }

    private boolean matches(String url) {
        for (String p : BLOCKED_PATTERNS) {
            if (url.contains(p)) return true;
        }
        return false;
    }
}