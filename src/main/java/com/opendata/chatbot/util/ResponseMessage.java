package com.opendata.chatbot.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseMessage {
    private final static Map<String, Map<String, Object>> m;
    private final static Map<String, Object> mm;

    static {
        m = new LinkedHashMap<>();
        mm = new LinkedHashMap<>();
    }

    public static String message(Integer code, String Message) {
        mm.put("code", code);
        mm.put("message", Message);
        m.put("status", mm);
        return JsonConverter.toJsonString(m);
    }
}
