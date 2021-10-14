package com.opendata.chatbot.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface LineService {
    ResponseEntity<String> WebHook(String requestBody, String line_headers);

    boolean validateLineHeader(String requestBody, String lineHeaders);

    ResponseEntity<String> replyMessage(String requestBody) throws Exception;

    ResponseEntity<String> replyWeatherForecast(String dist, String replyToken);

    ResponseEntity<String> pushMessage(String json);
}
