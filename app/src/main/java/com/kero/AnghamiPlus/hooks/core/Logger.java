package com.kero.anghamiplus.hooks.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.robv.android.xposed.XposedBridge;

public final class Logger {

    private Logger() {}

    private static final SimpleDateFormat timeFormat =
            new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);

    //══════════════════════════════════════════════════════
    // Banner (Optional)
    //══════════════════════════════════════════════════════
    public static void banner() {
        if (!Config.LOG_HOOKS) return;

        String header =
                "\n╔═══════════════════════════════════════════════════════╗" +
                "\n║      " + Config.MODULE_NAME + " v" + Config.MODULE_VERSION + " Activated           ║" +
                "\n║      Author: " + Config.MODULE_AUTHOR + "                          ║" +
                "\n╚═══════════════════════════════════════════════════════╝";
        XposedBridge.log(header);
    }

    //══════════════════════════════════════════════════════
    // Info Log
    //══════════════════════════════════════════════════════
    public static void i(String msg) {
        if (!Config.LOG_HOOKS) return;
        log(msg);
    }

    //══════════════════════════════════════════════════════
    // Debug Log (only in DEBUG_MODE)
    //══════════════════════════════════════════════════════
    public static void d(String msg) {
        if (Config.DEBUG_MODE) {
            log("[DEBUG] " + msg);
        }
    }

    //══════════════════════════════════════════════════════
    // Error Log (prints only basic info unless DEBUG_MODE=true)
    //══════════════════════════════════════════════════════
    public static void e(String msg, Throwable t) {
        if (Config.LOG_ERRORS) {
            log("[ERROR] " + msg +
                    (t != null ? (" | " + t.getClass().getSimpleName()) : ""));
        }

        if (Config.DEBUG_MODE && t != null) {
            t.printStackTrace();
        }
    }

    //══════════════════════════════════════════════════════
    // The actual logger
    //══════════════════════════════════════════════════════
    private static void log(String message) {
        String line = "[" + timeFormat.format(new Date()) + "] " + message;
        XposedBridge.log(line);
    }
}