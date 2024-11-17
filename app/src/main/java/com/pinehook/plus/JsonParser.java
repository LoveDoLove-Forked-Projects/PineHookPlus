package com.pinehook.plus;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class JsonParser {
    public static Map<String, Map<String, Object>> parseConfig(JSONObject jsonObject) throws JSONException {
        Map<String, Map<String, Object>> config = new HashMap<>();
        Iterator<String> classNames = jsonObject.keys();
        while (classNames.hasNext()) {
            String className = classNames.next();
            JSONObject classConfig = jsonObject.getJSONObject(className);
            Map<String, Object> methods = new HashMap<>();
            Iterator<String> methodNames = classConfig.keys();
            while (methodNames.hasNext()) {
                String methodName = methodNames.next();
                Object methodConfig = classConfig.get(methodName);
                if (methodConfig instanceof JSONObject) {
                    Map<String, Object> methodDetails = parseJsonObjectToMap((JSONObject) methodConfig);
                    methods.put(methodName, methodDetails);
                } else {
                    methods.put(methodName, methodConfig);
                }
            }
            config.put(className, methods);
        }
        return config;
    }

    @NonNull
    private static Map<String, Object> parseJsonObjectToMap(JSONObject jsonObject) throws JSONException {
        Map<String, Object> map = new HashMap<>();
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                // Recursively convert nested JSONObjects to Maps
                map.put(key, parseJsonObjectToMap((JSONObject) value));
            } else if (value instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) value;
                List<Object> list = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    Object arrayValue = jsonArray.get(i);
                    if (arrayValue instanceof JSONObject) {
                        list.add(parseJsonObjectToMap((JSONObject) arrayValue));
                    } else {
                        list.add(arrayValue);
                    }
                }
                map.put(key, list);
            } else {
                map.put(key, value);
            }
        }
        return map;
    }
}
