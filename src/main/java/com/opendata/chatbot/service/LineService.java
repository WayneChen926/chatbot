package com.opendata.chatbot.service;

import com.opendata.chatbot.entity.Event;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface LineService {
    ResponseEntity<String> WebHook(String requestBody, String line_headers);

    boolean validateLineHeader(String requestBody, String lineHeaders);

    ResponseEntity<String> replyMessage(String requestBody) throws Exception;

    ResponseEntity<String> replyTextDetermine(Event event,String replyToken);

    ResponseEntity<String> pushMessage(String json);
}
