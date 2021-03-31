package com.mark.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class GsonHandler {
    public static GsonHandler gsonHandler = new GsonHandler();
    private static Gson gson;

    public GsonHandler() {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        gson = builder.create();
    }

    public static Gson getHandler() {
        return gson;
    }
}
