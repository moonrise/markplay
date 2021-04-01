package com.mark;

import java.awt.*;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.prefs.Preferences;

public class Prefs {
    private static Preferences userPrefs;
    private static ArrayList<String> recentFiles;

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

    public static void setRecentFile(String recentFile) {
        if (recentFile == null) {
            return;
        }

        if (recentFiles == null) {
            recentFiles = new ArrayList<String>();
        }

        if (recentFiles.indexOf(recentFile) >= 0) {
            return;     // already there
        }

        recentFiles.add(0, recentFile);

        // enforce the max list size
        if (recentFiles.size() > getMaxRecentFiles()) {
            recentFiles.remove(recentFiles.size()-1);
        }

        StringWriter writer = new StringWriter();
        int recentFilesSize = recentFiles.size();
        for (int i=0; i<recentFilesSize; i++) {
            writer.write(recentFiles.get(i));
            if (i < recentFilesSize-1) {
                writer.write(";");
            }
        }

        userPrefs.put("recentFiles", writer.toString());
    }

    public static String[] getRecentFiles() {
        if (recentFiles == null) {
            recentFiles = new ArrayList(Arrays.asList(userPrefs.get("recentFiles", "").split(";")));
            if (recentFiles.get(0).trim().isEmpty()) {
                recentFiles = new ArrayList<>();
            }

        }
        return recentFiles.toArray(new String[] {});
    }

    public static int getMaxRecentFiles() {
        return userPrefs.getInt("maxRecentFiles", 12);
    }

    public static void setMaxRecentFiles(int x) {
        userPrefs.putInt("maxRecentFiles", x);
    }
}
