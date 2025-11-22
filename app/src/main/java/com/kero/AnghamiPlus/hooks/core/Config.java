package com.kero.anghamiplus.hooks.core;

public final class Config {

    private Config() {}

    //══════════════════════════════════════
    // Module Info
    //══════════════════════════════════════
    public static final String TARGET_PACKAGE = "com.anghami";     // Correct package
    public static final String MODULE_NAME    = "AnghamiPlus";
    public static final String MODULE_VERSION = "0.0.2";
    public static final String MODULE_AUTHOR  = "@Kero309x";

    //══════════════════════════════════════
    // Debug / Logging
    //══════════════════════════════════════
    public static boolean DEBUG_MODE   = false;   // Debug logging
    public static boolean LOG_HOOKS    = true;    // Show "✓ Hooked: ..." lines only
    public static boolean LOG_ERRORS   = true;    // Print errors if a hook fails

    //══════════════════════════════════════
    // Universal Hook Behavior
    //══════════════════════════════════════
    public static boolean AUTO_RELOAD_HOOKS      = true;   // inject hooks dynamically
    public static boolean UNIVERSAL_PLUS_GOLD    = true;   // auto apply isPlus/isGold everywhere
    public static boolean FORCE_PLUS_EVERYWHERE  = true;   // Always treat user as Plus/Gold

    //══════════════════════════════════════
    // Hook Sections (Master Toggles)
    //══════════════════════════════════════
    public static boolean ENABLE_ACCOUNT_HOOKS      = true;
    public static boolean ENABLE_AD_HOOKS           = true;
    public static boolean ENABLE_KARAOKE_HOOKS      = true;
    public static boolean ENABLE_SHUFFLE_HOOKS      = true;
    public static boolean ENABLE_HEADERBAR_HOOK     = true;
    public static boolean ENABLE_PLAYQUEUE_HOOKS    = true;
    public static boolean ENABLE_PREF_HELPER_HOOKS  = true;
    public static boolean ENABLE_NETWORK_BLOCK_HOOKS= true;
    public static boolean ENABLE_SESSION_HOOKS      = true;
    public static boolean ENABLE_DOWNLOAD_HOOKS     = true;

    //══════════════════════════════════════
    // Internal Runtime Flags
    //══════════════════════════════════════
    public static long FIRST_LOAD_TIME = System.currentTimeMillis();
    public static int HOOK_COUNT       = 0;  // Used to count successful hooks
}