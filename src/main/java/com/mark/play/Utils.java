package com.mark.play;

import org.apache.commons.lang3.time.DurationFormatUtils;

public class Utils {
    public static final String AppName = "MarkPlay";
    public static final String NoName = "<New>";

    // get the resource from the resources directory (packaged in the jar)
    public static String getResourcePath(String path) {
        return Utils.class.getResource(path).getPath();
    }

    public static String getTimelineFormatted(long timeMilli, boolean milli) {
        String time = DurationFormatUtils.formatDuration(timeMilli, "H:mm:ss.S", true);

        if (time.startsWith("0:")) {
            time = time.substring(2);
        }

        if (time.startsWith("00:")) {
            time = time.substring(3);
        }

        if (time.startsWith("0")) {
            time = time.substring(1);
        }

        return time.substring(0, time.length() - (milli ? 2 : 4));
    }
}
