package com.opendata.chatbot.repository;

import com.opendata.chatbot.dao.WeatherForecastDto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OpenDataRepo extends MongoRepository<WeatherForecastDto,String> {
    List<WeatherForecastDto> findByDistrict(String district);
    WeatherForecastDto findByDistrictAndCity(String district, String city);
}
