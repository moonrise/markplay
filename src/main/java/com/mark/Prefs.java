package com.mark;

import java.awt.*;
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

    public static Rectangle getMainFrameGeometry() {
        return Utils.stringToRect(userPrefs.get("mainFrameGeometry", "0, 0, 800, 600"));
    }

    public static void setMainFrameGeometry(Rectangle rect) {
        userPrefs.put("mainFrameGeometry", Utils.rectToString(rect));
        //dumpAll();
    }

    public static int getDividerX() {
        return userPrefs.getInt("dividerX", 150);
    }

    public static void setDividerX(int x) {
        userPrefs.putInt("dividerX", x);
    }

    public static void setNavigatorVisible(boolean visible) {
        userPrefs.putBoolean("navigatorVisible", visible);
    }

    public static boolean isNavigatorVisible() {
        return userPrefs.getBoolean("navigatorVisible", true);
    }

    // screen/monitor device id (not used at the moment - 3/30/2021)
    public static String getDeviceId() {
        return userPrefs.get("deviceId", null);
    }

    public static void setDeviceId(String deviceId) {
        userPrefs.put("deviceId", deviceId);
        //dumpAll();
    }

    public static void setMute(boolean visible) {
        userPrefs.putBoolean("mute", visible);
    }

    public static boolean isMute() {
        return userPrefs.getBoolean("mute", false);
    }

    public static int getVolume() {
        return userPrefs.getInt("volume", 100);
    }

    public static void setVolume(int x) {
        userPrefs.putInt("volume", x);
    }
}
