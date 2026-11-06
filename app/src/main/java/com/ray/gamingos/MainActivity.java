package com.ray.gamingos;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends Activity {
    private LinearLayout gameList;
    private String selectedPlayer = "PLAYER 1";

    private int dp(int n) {
        return (int)(n * getResources().getDisplayMetrics().density + 0.5f);
    }
    private GradientDrawable panel(int color, int stroke) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(dp(14));
        if (stroke != 0) d.setStroke(dp(1), stroke);
        return d;
    }
    private TextView label(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        buildHome();
    }
    private void buildHome() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(20), dp(24), dp(20), dp(28));
        page.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,
            new int[]{0xff071326, 0xff12365d, 0xff071326}));
        scroll.addView(page);
        setContentView(scroll);

        TextView logo = label("RAY  /  GAMING OS", 15, 0xff55ccff, true);
        logo.setLetterSpacing(.15f);
        page.addView(logo);

        TextView title = label("YOUR GAME\\nSPACE", 34, Color.WHITE, true);
        title.setPadding(0, dp(18), 0, dp(4)); page.addView(title);
        page.addView(label("Choose a player. Pick a game. Play.", 14, 0xffc6d9ed, false));

        // Illustrated home-screen artwork panel (no external image required).
        FrameLayout art = new FrameLayout(this);
        LinearLayout.LayoutParams artParams = new LinearLayout.LayoutParams(-1, dp(165));
        artParams.setMargins(0, dp(20), 0, dp(20)); art.setLayoutParams(artParams);
        art.setBackground(panel(0xff164b78, 0xff55ccff));
        TextView symbol = label("✦", 82, 0xff9be4ff, true);
        symbol.setGravity(Gravity.CENTER);
        art.addView(symbol, new FrameLayout.LayoutParams(-1, -1));
        TextView caption = label("LEVEL UP YOUR PLAY", 12, Color.WHITE, true);
        caption.setGravity(Gravity.CENTER); caption.setBackgroundColor(0x66071326);
        FrameLayout.LayoutParams cp = new FrameLayout.LayoutParams(-1, dp(34), Gravity.BOTTOM);
        art.addView(caption, cp); page.addView(art);

        page.addView(label("SELECT PLAYER", 13, 0xff55ccff, true));
        LinearLayout players = new LinearLayout(this);
        players.setOrientation(LinearLayout.HORIZONTAL);
        players.setPadding(0, dp(10), 0, dp(18));
        addPlayer(players, "PLAYER 1"); addPlayer(players, "PLAYER 2");
        page.addView(players);

        TextView welcome = label("WELCOME, " + selectedPlayer, 18, Color.WHITE, true);
        welcome.setPadding(0, 0, 0, dp(14)); page.addView(welcome);
        page.addView(label("GAME LIBRARY", 20, Color.WHITE, true));
        TextView hint = label("Apps installed on your phone that can open are listed below.", 12, 0xffc6d9ed, false);
        hint.setPadding(0, dp(4), 0, dp(12)); page.addView(hint);
        gameList = new LinearLayout(this); gameList.setOrientation(LinearLayout.VERTICAL);
        page.addView(gameList); loadApps();
    }
    private void addPlayer(LinearLayout row, String name) {
        TextView btn = label(name, 13, Color.WHITE, true);
        btn.setGravity(Gravity.CENTER); btn.setBackground(panel(
            name.equals(selectedPlayer) ? 0xff12608c : 0xff10243c, 0xff55ccff));
        btn.setPadding(dp(12), dp(12), dp(12), dp(12));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, dp(48), 1);
        p.setMargins(0, 0, dp(9), 0); row.addView(btn, p);
        btn.setOnClickListener(v -> { selectedPlayer = name; buildHome(); });
    }
    private void loadApps() {
        PackageManager pm = getPackageManager();
        Intent query = new Intent(Intent.ACTION_MAIN, null);
        query.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = pm.queryIntentActivities(query, 0);
        Collections.sort(apps, new Comparator<ResolveInfo>() {
            public int compare(ResolveInfo a, ResolveInfo b) {
                return a.loadLabel(pm).toString().compareToIgnoreCase(b.loadLabel(pm).toString());
            }
        });
        int shown = 0;
        for (ResolveInfo info : apps) {
            if (getPackageName().equals(info.activityInfo.packageName)) continue;
            LinearLayout item = new LinearLayout(this);
            item.setGravity(Gravity.CENTER_VERTICAL); item.setPadding(dp(12), dp(9), dp(12), dp(9));
            item.setBackground(panel(0xff10243c, 0xff244767));
            LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(-1, dp(64));
            ip.setMargins(0, 0, 0, dp(8)); gameList.addView(item, ip);
            ImageView icon = new ImageView(this); icon.setImageDrawable(info.loadIcon(pm));
            item.addView(icon, new LinearLayout.LayoutParams(dp(42), dp(42)));
            TextView name = label(info.loadLabel(pm).toString(), 15, Color.WHITE, true);
            name.setPadding(dp(12), 0, dp(8), 0);
            item.addView(name, new LinearLayout.LayoutParams(0, -2, 1));
            item.addView(label("PLAY  ›", 12, 0xff55ccff, true));
            item.setOnClickListener(v -> {
                Intent launch = pm.getLaunchIntentForPackage(info.activityInfo.packageName);
                if (launch != null) startActivity(launch);
                else Toast.makeText(this, "Unable to open this app", Toast.LENGTH_SHORT).show();
            });
            shown++;
        }
        if (shown == 0) gameList.addView(label("No launchable apps found.", 14, Color.WHITE, false));
    }
}
