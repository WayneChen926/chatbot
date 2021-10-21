package com.opendata.chatbot.errorHandler;

import com.opendata.chatbot.util.JsonConverter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ErrorMessageHandler {
    private final static Map<String, Map<String, Object>> m;
    private final static Map<String, Object> mm;

    static {
        m = new LinkedHashMap<>();
        mm = new LinkedHashMap<>();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ErrorMessage.class})
    public final String errorMessageHandler(ErrorMessage ex) {
        mm.put("code", ex.getCode());
        mm.put("message", ex.getMessage());
        m.put("status", mm);
        return JsonConverter.toJsonString(m);
    }
}
