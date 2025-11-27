package com.kero.anghamiplus.hooks.sections;

import com.kero.anghamiplus.hooks.core.ClassUtils;
import com.kero.anghamiplus.hooks.core.HookSection;
import com.kero.anghamiplus.hooks.core.Logger;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class SessionHooks implements HookSection {

    // إضافة GETlyrics + GETplaylists
    private static final String[] TARGET_ENDPOINTS = {
            "getplaylists.view",
            "getlyrics.view",
     "silo.anghami.com",
        "GETpurchases.view"
            
    };

    @Override
    public String getSectionName() {
        return "Session Spoofer (Replace SID for GETplaylists + GETlyrics)";
    }

    @Override
    public boolean install(XC_LoadPackage.LoadPackageParam lp) {
        try {
            Class<?> reqClass = ClassUtils.findClassSafely("okhttp3.Request", lp.classLoader);
            Class<?> httpUrlClass = ClassUtils.findClassSafely("okhttp3.HttpUrl", lp.classLoader);

            if (reqClass == null || httpUrlClass == null) {
                Logger.i("Missing OkHttp classes — cannot install");
                return false;
            }

            XposedHelpers.findAndHookMethod(
                    reqClass,
                    "url",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            try {
                                Object httpUrlObj = param.getResult();
                                if (httpUrlObj == null) return;

                                String url = httpUrlObj.toString();
                                String urlLower = url.toLowerCase();

                                // شغل الهوك على GETlyrics و GETplaylists
                                if (!isTarget(urlLower)) return;

                                Logger.d("🎯 Matched API → rewriting SID : " + urlLower);

                                String newSid = buildToken();

                                int q = url.indexOf('?');
                                String path = q >= 0 ? url.substring(0, q) : url;
                                String query = q >= 0 ? url.substring(q + 1) : "";

                                if (query.isEmpty()) {
                                    String newUrl = path + "?sid=" + newSid;
                                    Object parsed = XposedHelpers.callStaticMethod(httpUrlClass, "parse", newUrl);
                                    if (parsed != null) param.setResult(parsed);
                                    return;
                                }

                                String[] parts = query.split("&");
                                List<String> kept = new ArrayList<>(parts.length);

                                for (String p : parts) {
                                    if (p == null || p.isEmpty()) continue;
                                    int eq = p.indexOf('=');
                                    String name = eq > -1 ? p.substring(0, eq) : p;

                                    if (name.equalsIgnoreCase("sid")) continue;
                                    kept.add(p);
                                }

                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < kept.size(); i++) {
                                    if (i > 0) sb.append('&');
                                    sb.append(kept.get(i));
                                }

                                if (sb.length() > 0) sb.append('&');
                                sb.append("sid=").append(newSid);

                                String newUrl = path + "?" + sb.toString();

                                Object parsed = XposedHelpers.callStaticMethod(httpUrlClass, "parse", newUrl);
                                if (parsed != null) param.setResult(parsed);

                                Logger.d("✔ SID replaced successfully for → " + getBase(newUrl));

                            } catch (Throwable t) {
                                Logger.e("Session rewrite error", t);
                            }
                        }
                    }
            );

            Logger.i("✓ Session Spoofer for GETplaylists + GETlyrics installed");
            return true;

        } catch (Throwable e) {
            Logger.e("Failed to install Session Spoofer", e);
            return false;
        }
    }

    private boolean isTarget(String urlLower) {
        for (String e : TARGET_ENDPOINTS) {
            if (urlLower.contains(e)) return true;
        }
        return false;
    }

    private String buildToken() {
        return "i7%3Akicclhkf%3A3nqqn2664963oq4s%3Aecehddejdjcfhi%3ART%3Agcel%3Ara%3An8.0.2%3A182%3A%3An8.0.2%3A0%3Ana%3Aq2r7119pq3";
    }

    private String getBase(String url) {
        int q = url.indexOf('?');
        return q > 0 ? url.substring(0, q) : url;
    }
}