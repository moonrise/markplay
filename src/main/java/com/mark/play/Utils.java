package com.mark.play;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.awt.*;

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

    /**
     * @param rect string notation of "x,y,width,height"
     * @return Rectangle
     */
    public static Rectangle stringToRect(String rect) {
        String[] split = rect.split(",");
        return new Rectangle(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
    }

    /**
     * @param rect Rectangle
     * @return  string notation of "x,y,width,height"
     */
    public static String rectToString(Rectangle rect) {
        return String.format("%d,%d,%d,%d", rect.x, rect.y, rect.width, rect.height);
    }
}
