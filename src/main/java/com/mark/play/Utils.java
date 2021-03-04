package com.mark.play;

public class Utils {

    public static String getResourcePath(String path) {
        return Utils.class.getResource(path).getPath();
    }

    public static String getTimelineFormatted(float time, int width) {
        float seconds = time/1000F%60;
        int minutes = (int) (time/60000);
        int hours = minutes/60;
        return String.format("%s%s%."+width+"f", hours > 0 ? String.format("%d:", hours) : "", minutes > 0 ? String.format("%d:", minutes) : "", seconds);
    }
}
