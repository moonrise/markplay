package com.mark.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mark.Log;
import com.mark.resource.Resource;


public class GsonHandler {
    public static GsonHandler gsonHandler = new GsonHandler();
    private static Gson gson;

    public GsonHandler() {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        gson = builder.create();
    }

    public static String toJsonString(Resource resource) {
        String jsonString = gson.toJson(resource);
        Log.log("json string: %s", jsonString);
        return jsonString;
    }
}
