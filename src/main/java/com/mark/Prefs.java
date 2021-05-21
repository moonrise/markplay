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
        Prefs.getRecentFiles();
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

    public static void setNavigatorVisible(boolean x) {
        userPrefs.putBoolean("navigatorVisible", x);
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

    public static void setPlayOnLoad(boolean x) {
        userPrefs.putBoolean("playOnLoad", x);
    }

    public static boolean isPlayOnLoad() {
        return userPrefs.getBoolean("playOnLoad", false);
    }

    public static void setFocusOnPlayer(boolean x) {
        userPrefs.putBoolean("focusOnPlayer", x);
    }

    public static boolean isFocusOnPlayer() {
        return userPrefs.getBoolean("focusOnPlayer", true);
    }

    public static void setMute(boolean x) {
        userPrefs.putBoolean("mute", x);
    }

    public static boolean isMute() {
        return userPrefs.getBoolean("mute", false);
    }

    // TODO: volume normalization is very messy through out
    public static int getVolume() {
        return userPrefs.getInt("volume", 100);
    }

    public static void setVolume(int x) {
        userPrefs.putInt("volume", x);
    }

    public static String getRecentDirectory() {
        return userPrefs.get("recentDirectory", "");
    }

    public static void setRecentDirectory(String path) {
        userPrefs.put("recentDirectory", path);
    }

    public static void setRecentFile(String recentFile) {
        if (recentFile == null) {
            return;
        }

        if (recentFiles == null) {
            recentFiles = new ArrayList<String>();
        }

        // remove and add back to get the MRU right
        removeRecentFile(recentFile);
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

    public static void removeRecentFile(String path) {
        int index = recentFiles.indexOf(path);
        if (index >= 0) {
            recentFiles.remove(index);
        }
    }

    public static void clearRecentFiles() {
        recentFiles = null;
        userPrefs.put("recentFiles", "");
    }

    public static int getMaxRecentFiles() {
        return userPrefs.getInt("maxRecentFiles", 12);
    }

    public static void setMaxRecentFiles(int x) {
        userPrefs.putInt("maxRecentFiles", x);
    }

    public static void setModifiedConfirmOnClose(boolean x) {
        userPrefs.putBoolean("modifiedConfirm", x);
    }

    public static boolean isModifiedConfirmOnClose() {
        return userPrefs.getBoolean("modifiedConfirm", false);
    }

    public static void setAllowDuplicateResourcePath(boolean x) {
        userPrefs.putBoolean("allowDuplicateResourcePath", x);
    }

    public static boolean isAllowDuplicateResourcePath() {
        return userPrefs.getBoolean("allowDuplicateResourcePath", false);
    }

    public static int getSkipTimeTiny() {
        return userPrefs.getInt("skipTimeTiny", 2000);
    }

    public static void setSkipTimeTiny(int x) {
        userPrefs.putInt("skipTimeTiny", x);
    }

    public static int getSkipTimeSmall() {
        return userPrefs.getInt("skipTimeSmall", 5000);
    }

    public static void setSkipTimeSmall(int x) {
        userPrefs.putInt("skipTimeSmall", x);
    }

    public static int getSkipTimeMed() {
        return userPrefs.getInt("skipTimeMed", 30000);
    }

    public static void setSkipTimeMed(int x) {
        userPrefs.putInt("skipTimeMed", x);
    }

    public static int getSkipTimeLarge() {
        return userPrefs.getInt("skipTimeLarge", 300000);
    }

    public static void setSkipTimeLarge(int x) {
        userPrefs.putInt("skipTimeLarge", x);
    }

    public static void setPlaySelectedMarkers(boolean x) {
        userPrefs.putBoolean("playSelected", x);
    }

    public static boolean isPlaySelectedMarkers() {
        return userPrefs.getBoolean("playSelected", false);
    }

    // playtime fuzzy factor in millis to manage forward playing time (marker time search in the vicinity, etc...)
    public static int getTimeFuzzyFactor() {
        return userPrefs.getInt("timeFuzzyFactor", 500);
    }

    public static void setTimeFuzzyFactor(int x) {
        userPrefs.putInt("timeFuzzyFactor", x);
    }

    public static String[] getRootPrefixes() {
        String prefValue = userPrefs.get("roots", "");
        if (prefValue.isEmpty()) {
            return new String[]{};
        }
        return prefValue.split(";");
    }

    public static void setRootPrefixes(String[] prefixes) {
        StringWriter writer = new StringWriter();
        for (int i=0; i<prefixes.length; i++) {
            writer.write(prefixes[i]);
            if (i < prefixes.length-1) {
                writer.write(";");
            }
        }
        userPrefs.put("roots", writer.toString());
    }

    public static String getHashStoreDBPath() {
        return userPrefs.get("hashDB", "");
    }

    public static void setHashStoreDBPath(String dbPath) {
        userPrefs.put("hashDB", dbPath);
    }
}