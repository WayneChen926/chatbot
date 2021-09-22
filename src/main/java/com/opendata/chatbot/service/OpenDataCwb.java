package com.opendata.chatbot.service;

import com.opendata.chatbot.entity.Center;
import com.opendata.chatbot.entity.Location;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public interface OpenDataCwb {
    Center AllData();

    Location taipeiCwb(String district);

    String weatherForecast(String district);
}
