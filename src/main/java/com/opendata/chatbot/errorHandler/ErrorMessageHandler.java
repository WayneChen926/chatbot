package com.opendata.chatbot.errorHandler;

import com.opendata.chatbot.errorHandler.entity.ErroeResponse;
import com.opendata.chatbot.errorHandler.entity.Status;
import com.opendata.chatbot.util.JsonConverter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorMessageHandler {
    private final static ErroeResponse erroeResponse;
    private final static Status status;

    static {
        erroeResponse = new ErroeResponse();
        status = new Status();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ErrorMessage.class})
    public final String errorMessageHandler(ErrorMessage ex) {
        status.setCode(ex.getCode());
        status.setMessage(ex.getMessage());
        erroeResponse.setStatus(status);
        return JsonConverter.toJsonString(erroeResponse);
    }
}
