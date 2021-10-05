package com.opendata.chatbot.repository;

import com.opendata.chatbot.dao.WeatherForecastDto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OpenDataRepo extends MongoRepository<WeatherForecastDto,String> {
    Optional<WeatherForecastDto> findByDistrict(String district);
}
