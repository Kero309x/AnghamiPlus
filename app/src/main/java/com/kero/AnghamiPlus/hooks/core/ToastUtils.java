package com.kero.anghamiplus.hooks.core;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ToastUtils {

    private static final AtomicBoolean once = new AtomicBoolean(false);
    private static volatile Application appInstance = null;

    private ToastUtils() {}

    //═══════════════════════════════════════════════════
    // Try to get Application instance safely
    //═══════════════════════════════════════════════════
    private static Application getApp() {
        if (appInstance != null)
            return appInstance;

        try {
            Class<?> at = Class.forName("android.app.ActivityThread");
            Method curApp = at.getMethod("currentApplication");
            appInstance = (Application) curApp.invoke(null);
        } catch (Throwable ignored) {}

        return appInstance;
    }

    //═══════════════════════════════════════════════════
    // Show Toast safely
    //═══════════════════════════════════════════════════
    public static void show(String msg) {
        try {
            Application app = getApp();
            if (app == null) return;

            Handler main = new Handler(Looper.getMainLooper());
            main.post(() -> {
                try {
                    Toast.makeText(app.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                } catch (Throwable ignored) {}
            });

        } catch (Throwable ignored) {}
    }

    //═══════════════════════════════════════════════════
    // Show Toast only once per process
    //═══════════════════════════════════════════════════
    public static void showOnce(String msg) {
        if (once.compareAndSet(false, true)) {
            show(msg);
        }
    }
}