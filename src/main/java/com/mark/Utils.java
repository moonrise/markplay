package com.mark;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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

    public static int parseInt(String value) {
        return Integer.parseInt(value.trim());
    }

    /**
     * @param rect string notation of "x,y,width,height"
     * @return Rectangle
     */
    public static Rectangle stringToRect(String rect) {
        String[] split = rect.split(",");
        return new Rectangle(parseInt(split[0]), parseInt(split[1]), parseInt(split[2]), parseInt(split[3]));
    }

    /**
     * @param rect Rectangle
     * @return  string notation of "x,y,width,height"
     */
    public static String rectToString(Rectangle rect) {
        return String.format("%d,%d,%d,%d", rect.x, rect.y, rect.width, rect.height);
    }


    /**
     * circular mod (modulo)
     * @param a
     * @param b (divisor)
     * @return
     */
    public static int mod(int a, int b) {
        int m = a % b;
        return m < 0 ? m + b : m;
    }

    public static String computeFileHash(String filePath) {
        String checksum = null;
        try {
            checksum = DigestUtils.md5Hex(new FileInputStream(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return checksum;
    }

    // normalize the path (i.e. between Linux and Windows, etc...)
    public static String normPath(String path) {
        return new File(path).getPath();
    }

    public static boolean normPathIsEqual(String path1, String path2) {
        return normPath(path1).equals(normPath(path2));
    }

    public static int normPathIndexOf(String pathIn, String pathOf) {
        return normPath(pathIn).indexOf(normPath(pathOf));
    }

    public static boolean normPathStartsWith(String pathIn, String pathOf) {
        return normPath(pathIn).startsWith(normPath(pathOf));
    }

    public static void sleep(int milliSeconds) {
        try {
            Thread.sleep(milliSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}