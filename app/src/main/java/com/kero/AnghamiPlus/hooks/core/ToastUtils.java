package com.kero.anghamiplus.hooks.core;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ToastUtils {
    private static final AtomicBoolean once = new AtomicBoolean(false);
    private ToastUtils() {}

    private static Application getApplication() {
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Method currentApp = activityThread.getMethod("currentApplication");
            return (Application) currentApp.invoke(null);
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static void show(String msg) {
        try {
            new Handler(Looper.getMainLooper()).post(() -> {
                try {
                    Application app = getApplication();
                    if (app != null)
                        Toast.makeText(app, msg, Toast.LENGTH_LONG).show();
                } catch (Throwable ignored) {}
            });
        } catch (Throwable ignored) {}
    }

    public static void showOnce(String msg) {
        if (once.compareAndSet(false, true)) show(msg);
    }
}