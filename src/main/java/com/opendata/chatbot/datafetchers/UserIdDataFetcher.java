package com.opendata.chatbot.datafetchers;


import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import com.opendata.chatbot.dao.User;
import com.opendata.chatbot.entity.WeatherForecast;
import com.opendata.chatbot.service.UserService;
import com.opendata.chatbot.service.WeatherForecastService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@DgsComponent
@Slf4j
public class UserIdDataFetcher {
    @Autowired
    private WeatherForecastService weatherForecastServiceImpl;

    @Autowired
    private UserService userServiceImpl;

    @DgsQuery
    public List<WeatherForecast> showWeatherForecast(@InputArgument String city, @InputArgument String ds) {
        var optionalWeatherForecastDto = weatherForecastServiceImpl.findByDistrictAndCity(ds, city);
        log.info("optionalWeatherForecastDto :{}", optionalWeatherForecastDto);
        AtomicReference<List<WeatherForecast>> s = new AtomicReference<>();
        List<WeatherForecast> weatherForecastList = null;
        if (optionalWeatherForecastDto != null) {
            weatherForecastList = optionalWeatherForecastDto.getWeatherForecast();
        }
        return weatherForecastList;
    }

    @DgsQuery
    public List<WeatherForecast> showWeatherForecastForDs(@InputArgument String ds) {
        List<WeatherForecast> weatherForecastList = new ArrayList<>();
        AtomicInteger n = new AtomicInteger(0);
        weatherForecastServiceImpl.findByDistrict(ds).forEach(weatherForecastDtoList -> {
            weatherForecastDtoList.getWeatherForecast().forEach(weatherForecast -> {
                weatherForecastList.add(n.getAndIncrement(), weatherForecast);
            });
        });
        return weatherForecastList;
    }

    @DgsMutation
    @Transactional(rollbackFor = Exception.class)
    public User createPromotion(@Validated @InputArgument("input") User user) {
        return userServiceImpl.saveUser(user);
    }
}
