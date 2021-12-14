package com.opendata.chatbot.service;

import com.opendata.chatbot.entity.Location;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OpenDataCwb {
    String AllData(String url);

    String replaceVariable(String url, String[] vars);

    void cityCwb();

    void weatherForecast(String locationsName, List<Location> locationList);
}
