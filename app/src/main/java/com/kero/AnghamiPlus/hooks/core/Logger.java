package com.kero.anghamiplus.hooks.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.robv.android.xposed.XposedBridge;

public final class Logger {
    private Logger() {}

    private static final SimpleDateFormat timeFormat =
            new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);

    public static void banner() {
        String banner =
                "\n╔═══════════════════════════════════════════════════════╗" +
                "\n║  " + Config.MODULE_NAME + " v" + Config.MODULE_VERSION + " - Plus Unlocker for Anghami      ║" +
                "\n║  Author: " + Config.MODULE_AUTHOR + " | License: MIT | GitHub Ready  ║" +
                "\n╚═══════════════════════════════════════════════════════╝";
        XposedBridge.log(banner);
    }

    public static void d(String msg) {
        if (Config.DEBUG_MODE) log("[DEBUG] " + msg);
    }

    public static void i(String msg) {
        log(msg);
    }

    public static void e(String msg, Throwable t) {
        log("[ERROR] " + msg + (t != null ? (" | " + t.getClass().getSimpleName() + ": " + t.getMessage()) : ""));
        if (Config.DEBUG_MODE && t != null) t.printStackTrace();
    }

    private static void log(String message) {
        String log = "[" + timeFormat.format(new Date()) + "] " + message;
        XposedBridge.log(log);
    }
}