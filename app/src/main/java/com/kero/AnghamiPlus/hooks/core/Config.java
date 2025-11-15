package com.kero.anghamiplus.hooks.core;

public final class Config {
    private Config() {}

    public static final String TARGET_PACKAGE = "com.anghami";
    public static final String MODULE_NAME = "AnghamiPlus";
    public static final String MODULE_VERSION = "0.0.1";
    public static final String MODULE_AUTHOR = "@Kero309x";

    public static final boolean DEBUG_MODE = false;

    public static final int INSTALL_DELAY_MS = 2000;

    // Feature Toggles
    public static final boolean ENABLE_ACCOUNT_HOOKS = true;
    public static final boolean ENABLE_AD_HOOKS = true;
    public static final boolean ENABLE_KARAOKE_HOOKS = true;
    public static final boolean ENABLE_SHUFFLE_HOOKS = true;
    public static final boolean ENABLE_HEADERBAR_HOOK = true;

   public static final boolean ENABLE_PLAYQUEUE_HOOKS = true;
    public static final boolean ENABLE_PREF_HELPER_HOOKS = true;
}