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

    public static Map<String, Map<String, Object>> message(Integer code, String Message) {
        mm.put("code", code);
        mm.put("message", Message);
        m.put("Status", mm);
        return m;
    }
}
