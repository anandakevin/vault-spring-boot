package com.example.demo.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Response {
    private Map <String, Object> body = new LinkedHashMap<>();

    public void put(String key, Object value) {
        body.put(key, value);
    }

    public Object get(String key) {
        return body.get(key);
    }

    public void printBody() {
        for(Map.Entry<String, Object> entry : body.entrySet()) {
            System.out.printf("%s: %s\n", entry.getKey(), formatResult(entry.getValue()));
        }
    }

    private String formatResult(Object result) {
        if(result instanceof List) {
            List<Object> list = (List<Object>) result;
            if(list.isEmpty()) {
                return "[]";
            }
            return list.toString();
        }

        return result.toString();
    }
}
