package com.opendata.chatbot.service;

import com.opendata.chatbot.entity.Location;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OpenDataCwb {
    String AllData(String url);

    List<Location> cityCwb(String city);

    void weatherForecast(String city);
}
