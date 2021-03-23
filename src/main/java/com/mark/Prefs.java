package com.mark;

import com.mark.play.Log;

import java.util.prefs.Preferences;

public class Prefs {
    private static Preferences userPrefs;

    static {
        userPrefs = Preferences.userNodeForPackage(Prefs.class);
    }

    public static void dumpAll() {
        try {
            String[] keys = userPrefs.keys();
            Log.log("-------------------- Preferences dump --------------------");
            for (String key: keys) {
                Log.log("%s : %s", key, userPrefs.get(key, "no values set"));
            }
        }
        catch (Exception e) {
            Log.err("Error in iterating through user preferences: " + e.toString());
        }
    }

    public static int getDividerX() {
        return userPrefs.getInt("dividerX", 150);
    }

    public static void setDividerX(int x) {
        userPrefs.putInt("dividerX", x);
    }

    public static void setNavigatorVisible(boolean visible) {
        userPrefs.putBoolean("navigatorVisible", visible);
        //dumpAll();
    }

    public static boolean isNavigatorVisible() {
        return userPrefs.getBoolean("navigatorVisible", true);
    }
}
