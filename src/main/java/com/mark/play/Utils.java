package com.mark.play;

public class Utils {

    public static String getResourcePath(String path) {
        return Utils.class.getResource(path).getPath();
    }
}
