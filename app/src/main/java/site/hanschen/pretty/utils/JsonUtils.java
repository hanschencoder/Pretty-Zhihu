package site.hanschen.pretty.utils;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class JsonUtils {

    private JsonUtils() {
    }

    private static Gson gson = new Gson();

    public static <T> T fromJsonObject(String jsonStr, Class<T> targetClass) {
        return gson.fromJson(jsonStr, targetClass);
    }

    public static <T> List<T> fromJsonArray(String jsonStr) {
        return gson.fromJson(jsonStr, new TypeToken<List<T>>() {
        }.getType());
    }

    public static String toJson(Object o) {
        return gson.toJson(o);
    }
}
