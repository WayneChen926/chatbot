package com.opendata.chatbot.errorHandler;

import lombok.Data;

@Data
public class ErrorMessage extends RuntimeException {
    private String code;
    private String message;

    public ErrorMessage(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
