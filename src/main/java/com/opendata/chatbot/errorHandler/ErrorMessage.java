package com.opendata.chatbot.errorHandler;

public class ErrorMessage extends RuntimeException {
    private final String code;
    private final String message;

    public ErrorMessage(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
