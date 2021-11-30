package com.opendata.chatbot.service;

import com.opendata.chatbot.dao.WeatherForecastDto;
import com.opendata.chatbot.entity.Messages;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface LineService {
    ResponseEntity<String> WebHook(String requestBody, String line_headers);

    boolean validateLineHeader(String requestBody, String lineHeaders);

    void replyMessage(String requestBody) throws Exception;

    ResponseEntity<String> replyWeatherForecast(String dist, String replyToken);

    ResponseEntity<String> replyWeatherLocation(String city, String dist, String replyToken);
    Messages weatherForecastLineMessageReply(WeatherForecastDto openData);
    ResponseEntity<String> pushMessage(String json);
}
