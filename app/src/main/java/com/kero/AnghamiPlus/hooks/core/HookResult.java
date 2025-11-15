package com.kero.anghamiplus.hooks.core;

public final class HookResult {
    public final String name;
    public final boolean success;
    public final long ms;
    public final String error;
    public final boolean requiresRestart;

    public HookResult(String name, boolean success, long ms, String error, boolean requiresRestart) {
        this.name = name;
        this.success = success;
        this.ms = ms;
        this.error = error;
        this.requiresRestart = requiresRestart;
    }
}