package com.kero.anghamiplus.hooks.sections;

import com.kero.anghamiplus.hooks.core.BoolHook;
import com.kero.anghamiplus.hooks.core.HookSection;
import com.kero.anghamiplus.hooks.core.Logger;
import com.kero.anghamiplus.hooks.core.IntHook;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class AccountHooks implements HookSection {

    @Override public String getSectionName() { return "Account Plus"; }

    @Override public boolean requiresRestart() { return true; }

    @Override
    public boolean install(XC_LoadPackage.LoadPackageParam lp) {

        Logger.i("  ┌─ Installing Account/Plus/Gold hooks…");

        int success = 0;
        int total = 0;

        //═══════════════════════════════════════════════
        // FALSE HOOKS
        //═══════════════════════════════════════════════
        String[][] falseHooks = {
                {"com.anghami.ghost.local.Account", "isShowKaraoke"},
                {"com.anghami.ghost.local.Account", "isShowKaraokeUpsellButton"},
                {"com.anghami.ghost.local.Account", "shouldShowKaraokePaywall"},
                {"com.anghami.ghost.model.proto.ProtoAccount$Account", "getEnablePlayerRestrictions"},
                {"com.anghami.ghost.prefs.PreferenceHelper", "getHasRestrictedQueue"}
        };

        for (String[] hook : falseHooks) {
            total++;
            if (BoolHook.hook(lp, hook[0], hook[1], false)) {
                Logger.i("  │   ✓ Hooked: " + hook[0] + "." + hook[1]);
                success++;
            }
        }

        //═══════════════════════════════════════════════
        // TRUE HOOKS
        //═══════════════════════════════════════════════
        String[][] trueHooks = {
                {"com.anghami.ghost.local.Account", "isGoldUser"},
                {"com.anghami.ghost.local.Account", "isPlusUser"},
                {"com.anghami.ghost.model.proto.ProtoAccount$Account", "getCanGoLive"},
                {"com.anghami.ghost.utils.GoldUtilsKt", "isGold", "com.anghami.ghost.pojo.Profile"},
                {"com.anghami.ghost.utils.GoldUtilsKt", "isGold", "com.anghami.ghost.pojo.RankedUser"},
                {"com.anghami.ghost.utils.GoldUtilsKt", "isGold", "com.anghami.ghost.pojo.stories.Story$User"},
                {"com.anghami.ghost.local.Account", "isPlus"},
                {"com.anghami.ghost.local.Account", "isGold"}
        };

        for (String[] hook : trueHooks) {
            total++;
            boolean ok;

            if (hook.length == 3) {
                ok = BoolHook.hook(lp, hook[0], hook[1], true, getParamClass(hook[2], lp.classLoader));
            } else {
                ok = BoolHook.hook(lp, hook[0], hook[1], true);
            }

            if (ok) {
                Logger.i("  │   ✓ Hooked: " + hook[0] + "." + hook[1]);
                success++;
            }
        }

        //═══════════════════════════════════════════════
        // INT HOOKS
        //═══════════════════════════════════════════════
        total++;
        if (IntHook.hookStringMethod(lp,
                "com.anghami.ghost.local.Account$PlanType",
                "getValue",
                "7")) {
            Logger.i("  │   ✓ Hooked: Account$PlanType.getValue");
            success++;
        }

        total++;
        if (IntHook.hook(lp,
                "com.anghami.ghost.model.proto.ProtoAccount$Account",
                "getMaxOfflineSongs",
                999999)) {
            Logger.i("  │   ✓ Hooked: ProtoAccount.getMaxOfflineSongs");
            success++;
        }

        total++;
        if (IntHook.hook(lp,
                "com.anghami.ghost.model.proto.ProtoAccount$Account",
                "getMaxOfflineTime",
                999999)) {
            Logger.i("  │   ✓ Hooked: ProtoAccount.getMaxOfflineTime");
            success++;
        }

        //═══════════════════════════════════════════════
        // UNIVERSAL HOOKS
        //═══════════════════════════════════════════════
        setupUniversalHooks();
        Logger.i("  │   ✓ Hooked: Universal isPlus/isGold");


        //═══════════════════════════════════════════════
        // SPECIFIC STATIC isPlus() OVERRIDE
        //═══════════════════════════════════════════════
        try {
            Class<?> accClass = Class.forName(
                    "com.anghami.ghost.local.Account",
                    false,
                    lp.classLoader
            );

            XposedBridge.hookAllMethods(accClass, "isPlus", new XC_MethodHook() {
                @Override protected void afterHookedMethod(MethodHookParam param) {
                    param.setResult(true);
                }
            });

            Logger.i("  │   ✓ Hooked: Account.isPlus() → true (static/global)");

        } catch (Throwable t) {
            Logger.i("  │   ✗ Failed to hook static isPlus(): " + t.getMessage());
        }


        //═══════════════════════════════════════════════
        // SUMMARY
        //═══════════════════════════════════════════════
        Logger.i("  └─ Hooked " + success + "/" + total + " methods.");

        return success > 0;
    }

    //═══════════════════════════════════════════════
    // UNIVERSAL HOOKS
    //═══════════════════════════════════════════════
    private void setupUniversalHooks() {
        XC_MethodHook forceTrue = new XC_MethodHook() {
            @Override protected void afterHookedMethod(MethodHookParam p) {
                if (p.getResult() instanceof Boolean)
                    p.setResult(true);
            }
        };

        try {
            XposedBridge.hookAllMethods(Object.class, "isPlus", forceTrue);
            XposedBridge.hookAllMethods(Object.class, "isGold", forceTrue);
            XposedBridge.hookAllMethods(Object.class, "isPlusUser", forceTrue);
            XposedBridge.hookAllMethods(Object.class, "isGoldUser", forceTrue);
        } catch (Throwable ignored) {}
    }

    //═══════════════════════════════════════════════
    // PARAMETERS CLASS RESOLVER
    //═══════════════════════════════════════════════
    private Class<?> getParamClass(String className, ClassLoader loader) {
        try {
            return Class.forName(className, false, loader);
        } catch (Throwable e) {
            return null;
        }
    }
}