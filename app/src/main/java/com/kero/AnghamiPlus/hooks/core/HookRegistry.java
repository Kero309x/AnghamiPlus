package com.kero.anghamiplus.hooks.core;

import java.util.LinkedHashMap;
import java.util.Map;

public final class HookRegistry {

    private final Map<String, HookResult> results = new LinkedHashMap<>();

    public void add(HookResult r) {
        results.put(r.name, r);
    }

    public boolean isEmpty() {
        return results.isEmpty();
    }

    public void printSummary() {
        if (!Config.LOG_HOOKS) return;       // 🔥 احترم إعدادات اللوج

        if (results.isEmpty()) return;

        int ok = 0;
        int fail = 0;

        for (HookResult r : results.values()) {
            if (r.success) ok++;
            else fail++;
        }

        Logger.i("┌───────────────────────────────────────────────┐");
        Logger.i("│                HOOK SUMMARY                   │");
        Logger.i("├───────────────────────────────────────────────┤");

        Logger.i(String.format(
                "│  Total: %-3d |  Success: %-3d |  Failed: %-3d       │",
                results.size(), ok, fail
        ));

        Logger.i("└───────────────────────────────────────────────┘");

        // ⚠ فقط في حالة DEBUG_MODE نعرض الأخطاء بالتفصيل
        if (fail > 0 && Config.DEBUG_MODE && Config.LOG_ERRORS) {
            Logger.i("\n⚠️ Failed Hooks:");
            for (HookResult r : results.values()) {
                if (!r.success) {
                    Logger.i("  • " + r.name +
                            " → " + (r.error != null ? r.error : "Unknown error"));
                }
            }
            Logger.i("");
        }
    }
}