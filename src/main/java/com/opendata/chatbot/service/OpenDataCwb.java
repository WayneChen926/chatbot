package com.opendata.chatbot.service;

import com.opendata.chatbot.dto.WeatherForecastDto;
import com.opendata.chatbot.entity.Center;
import com.opendata.chatbot.entity.Location;
import org.springframework.stereotype.Service;

@Service
public interface OpenDataCwb {
    String AllData(String url);

    Location taipeiCwb(String district);

    WeatherForecastDto weatherForecast(String district);
}
