package com.grantgzd.kthttp.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.lang.reflect.Type;

public class GsonUtils {
    private static Gson gson;

    public static void setGson(Gson gson) {
        if (gson == null) {
            throw new NullPointerException("gson == null");
        }
        GsonUtils.gson = gson;
    }

    public static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .serializeNulls()
                    .create();
        }
        return gson;
    }

    public static String toJson(Object obj) {
        return getGson().toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return getGson().fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return getGson().fromJson(json, typeOfT);
    }

    public static <T> T fromJson(Reader reader, Type typeOfT) {
        return getGson().fromJson(reader, typeOfT);
    }

    private GsonUtils() { throw new AssertionError("no instance"); }
}
