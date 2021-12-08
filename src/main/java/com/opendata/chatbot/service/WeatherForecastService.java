package com.opendata.chatbot.service;

import com.opendata.chatbot.dao.WeatherForecastDto;

import java.util.List;

public interface WeatherForecastService {
    List<WeatherForecastDto> findByDistrict(String district);
    WeatherForecastDto findByDistrictAndCity(String district, String city);
}
