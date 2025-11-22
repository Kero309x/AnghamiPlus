package com.kero.anghamiplus.hooks.sections;

import com.kero.anghamiplus.hooks.core.ClassUtils;
import com.kero.anghamiplus.hooks.core.HookSection;
import com.kero.anghamiplus.hooks.core.Logger;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class SessionHooks implements HookSection {

    @Override
    public String getSectionName() {
        return "Session Spoofer";
    }

    private static final String[] TARGET_ENDPOINTS = {
            "lyrics",
            "playlists",
    };

    @Override
    public boolean install(XC_LoadPackage.LoadPackageParam lp) {
        Logger.i("  ┌─ Installing Session Spoofer…");

        try {
            Class<?> builder = ClassUtils.findClassSafely("okhttp3.Request$Builder", lp.classLoader);

            if (builder == null) {
                Logger.i("  └─ ⚠️ OkHttp Builder not found — skipping");
                return false;
            }

            XposedHelpers.findAndHookMethod(
                    builder,
                    "build",
                    new XC_MethodHook() {

                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {

                            try {
                                Object b = param.thisObject;
                                Object httpUrl = XposedHelpers.getObjectField(b, "url");
                                if (httpUrl == null) return;

                                String url = httpUrl.toString().toLowerCase();

                                if (!isTarget(url)) return;

                                String newToken = buildToken();
                                String newUrl;

                                if (url.contains("sid=")) {
                                    newUrl = url.replaceAll("sid=[^&]*", "sid=" + newToken);
                                } else {
                                    newUrl = url + (url.contains("?") ? "&" : "?") + "sid=" + newToken;
                                }

                                Object parsed = XposedHelpers.callStaticMethod(
                                        httpUrl.getClass(),
                                        "parse",
                                        newUrl
                                );

                                if (parsed != null) {
                                    XposedHelpers.setObjectField(b, "url", parsed);
                                }

                                // silent logging 5%
                                if (Math.random() > 0.95) {
                                    Logger.d("Session spoofed → " + getBase(url));
                                }

                            } catch (Throwable ignore) {
                                // Silent — no crash
                            }
                        }
                    }
            );

            Logger.i("  └─ ✓ Session Spoofer installed");
            return true;

        } catch (Throwable e) {
            Logger.e("  └─ Session hook failed", e);
            return false;
        }
    }

    private boolean isTarget(String url) {
        for (String e : TARGET_ENDPOINTS) {
            if (url.contains(e)) return true;
        }
        return false;
    }

    private String buildToken() {
        // safer dynamic static-like token
        return "i7%3A"
                + "kicclhkf%3A"
                + "3754o6253o81q76o%3A"
                + "ecehdddldiegfk%3A"
                + "RT%3A"
                + "gcel%3A"
                + "ra%3A"
                + "n8.0.2%3A"
                + "180%3A%3A"
                + "n8.0.2%3A"
                + "0%3A"
                + "na%3A"
                + "523r4191s4";
    }

    private String getBase(String url) {
        int q = url.indexOf('?');
        return q > 0 ? url.substring(0, q) : url;
    }
}