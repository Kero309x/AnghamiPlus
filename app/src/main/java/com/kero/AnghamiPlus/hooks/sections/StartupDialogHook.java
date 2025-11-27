package com.kero.anghamiplus.hooks.sections;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;
import android.net.Uri;

import com.kero.anghamiplus.hooks.core.HookSection;
import com.kero.anghamiplus.hooks.core.Logger;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class StartupDialogHook implements HookSection {

    @Override
    public String getSectionName() {
        return "Startup Dialog";
    }

    @Override
    public boolean install(XC_LoadPackage.LoadPackageParam lpparam) {

        try {
            XposedHelpers.findAndHookMethod(
                    "com.anghami.app.main.MainActivity",
                    lpparam.classLoader,
                    "onCreate",
                    android.os.Bundle.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {

                            Activity act = (Activity) param.thisObject;

                            SharedPreferences prefs = act.getSharedPreferences("kero_hooks", Activity.MODE_PRIVATE);
                            boolean shown = prefs.getBoolean("startup_dialog_shown", false);
                            if (shown) return;

                            prefs.edit().putBoolean("startup_dialog_shown", true).apply();

                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                safeShowDialog(act);
                            }, 1500);
                        }
                    }
            );

            Logger.i("  └─ ✓ StartupDialog Installed");
            return true;

        } catch (Throwable e) {
            Logger.e("StartupDialog Failed", e);
            return false;
        }
    }

    //=====================================================
    // FIXED SAFE DIALOG SHOW
    //=====================================================
    private void safeShowDialog(Activity act) {

        // 1) Activity not valid
        if (act == null) return;
        if (act.isFinishing()) return;
        if (act.isDestroyed()) return;

        // 2) window not ready yet → wait for UI to attach
        ViewGroup decor = (ViewGroup) act.getWindow().getDecorView();
        decor.post(() -> {

            if (act.isFinishing() || act.isDestroyed()) return;

            try {
                showDialog(act);
            } catch (Throwable e) {
                Logger.e("Dialog Failed (Window not ready)", e);
            }
        });
    }

    //=====================================================
    // DIALOG UI
    //=====================================================
    private void showDialog(Activity act) {

        Dialog dialog = new Dialog(act);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnKeyListener((d, keyCode, event) ->
                keyCode == KeyEvent.KEYCODE_BACK
        );

        LinearLayout root = new LinearLayout(act);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(65, 65, 65, 65);
        root.setGravity(Gravity.CENTER);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#1A1A1A"));
        bg.setCornerRadius(45);
        bg.setStroke(3, Color.parseColor("#7B43FF"));
        root.setBackground(bg);

        TextView title = new TextView(act);
        title.setText("🎵 AnghamiPlus Activated");
        title.setTextColor(Color.WHITE);
        title.setTextSize(22f);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 25);
        root.addView(title);

        TextView sub = new TextView(act);
        sub.setText("Your premium features are now enabled.");
        sub.setTextColor(Color.parseColor("#CCCCCC"));
        sub.setTextSize(15f);
        sub.setGravity(Gravity.CENTER);
        sub.setPadding(0, 0, 0, 35);
        root.addView(sub);

        addFeature(act, root, "✔ Ads Completely Removed");
        addFeature(act, root, "✔ Unlimited Skips Enabled");
        addFeature(act, root, "✔ Gold Profile Unlocked");
        addFeature(act, root, "✔ Unlimited Downloads");
        addFeature(act, root, "✔ Queue Restrictions Disabled");

        Button join = new Button(act);
        join.setText("Join Telegram");
        join.setPadding(25, 12, 25, 12);
        join.setTextColor(Color.WHITE);
        join.setBackgroundColor(Color.parseColor("#5A2BFF"));
        join.setAllCaps(false);
        join.setTextSize(16f);
        root.addView(join);

        join.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://t.me/its_kero309x"));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            act.startActivity(i);
        });

        Button ok = new Button(act);
        ok.setText("OK (30s)");
        ok.setEnabled(false);
        ok.setAlpha(0.35f);
        ok.setPadding(25, 12, 25, 12);
        ok.setTextColor(Color.WHITE);
        ok.setBackgroundColor(Color.parseColor("#27AE60"));
        ok.setAllCaps(false);
        ok.setTextSize(16f);
        root.addView(ok);

        ok.setOnClickListener(v -> dialog.dismiss());

        dialog.setContentView(root);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            window.setLayout(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        dialog.show();

        final int[] seconds = {30};
        Handler timerHandler = new Handler(Looper.getMainLooper());

        Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (seconds[0] > 0) {
                    ok.setText("OK (" + seconds[0] + "s)");
                    seconds[0]--;
                    timerHandler.postDelayed(this, 1000);
                } else {
                    ok.setEnabled(true);
                    ok.setAlpha(1f);
                    ok.setText("OK");
                }
            }
        };

        timerHandler.post(timerRunnable);
    }

    private void addFeature(Activity act, LinearLayout root, String text) {
        TextView f = new TextView(act);
        f.setText(text);
        f.setTextColor(Color.parseColor("#E0E0E0"));
        f.setTextSize(15f);
        f.setPadding(0, 0, 0, 20);
        root.addView(f);
    }
}