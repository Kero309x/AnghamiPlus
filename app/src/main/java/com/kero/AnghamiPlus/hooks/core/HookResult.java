package com.kero.anghamiplus.hooks.core;

public final class HookResult {

    public final String name;       // Section name
    public final boolean success;   // Installed or not
    public final long durationMs;   // Execution time
    public final String error;      // Error type (if any)

    public HookResult(String name, boolean success, long durationMs, String error) {
        this.name = name;
        this.success = success;
        this.durationMs = durationMs;
        this.error = error;
    }
}