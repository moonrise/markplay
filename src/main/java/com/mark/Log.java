package com.mark;

import java.util.Formatter;

public class Log {
    /**
     * just like String.format except that it goes to stdout
     * @param format
     * @param args
     */
    public static void log(String format, Object... args) {
        System.out.println((new Formatter()).format(format, args).toString());
    }

    /**
     * just like String.format except that it goes to stderr
     * @param format
     * @param args
     */
    public static void err(String format, Object... args) {
        System.err.println((new Formatter()).format(format, args).toString());
    }
}
