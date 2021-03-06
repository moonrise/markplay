package com.mark.play;

import org.apache.commons.lang3.time.DurationFormatUtils;

public class Utils {

    public static String getResourcePath(String path) {
        return Utils.class.getResource(path).getPath();
    }

    public static String getTimelineFormatted(long timeMilli, int width) {
        String time = DurationFormatUtils.formatDuration(timeMilli, "H:mm:ss", true);

        if (time.startsWith("0:")) {
            time = time.substring(2);
        }

        if (time.startsWith("00:")) {
            time = time.substring(3);
        }

        if (time.startsWith("0")) {
            time = time.substring(1);
        }

        return time;
    }
}
