package com.kero.anghamiplus.hooks.core;

import java.util.LinkedHashMap;
import java.util.Map;

public final class HookRegistry {
    private final Map<String, HookResult> results = new LinkedHashMap<>();

    public void add(HookResult r) { results.put(r.name, r); }

    public boolean isEmpty() { return results.isEmpty(); }

    public void printSummary() {
        if (results.isEmpty()) return;
        int ok = 0, fail = 0, restart = 0;
        for (HookResult r : results.values()) {
            if (r.success) { ok++; if (r.requiresRestart) restart++; }
            else fail++;
        }
        Logger.i("\n┌─────────────────────────────────────────────┐");
        Logger.i("│              HOOK SUMMARY                   │");
        Logger.i("├─────────────────────────────────────────────┤");
        Logger.i(String.format("│  Total: %-3d | ✅ Success: %-3d | ❌ Failed: %-3d │", results.size(), ok, fail));
        if (restart > 0) Logger.i(String.format("│  🔄 Restart Required: %-25d │", restart));
        Logger.i("└─────────────────────────────────────────────┘\n");

        if (fail > 0 && Config.DEBUG_MODE) {
            Logger.i("⚠️  Failed Hooks:");
            for (HookResult r : results.values()) {
                if (!r.success) Logger.i("  • " + r.name + " → " + (r.error != null ? r.error : "Unknown error"));
            }
        }
    }
}