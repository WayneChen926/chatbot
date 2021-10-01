package com.opendata.chatbot.datafetchers;


import com.fasterxml.jackson.core.type.TypeReference;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import com.opendata.chatbot.entity.WeatherForecast;
import com.opendata.chatbot.repository.OpenDataRepo;
import com.opendata.chatbot.service.OpenDataCwb;
import com.opendata.chatbot.util.JsonConverter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;

@DgsComponent
public class UserIdDataFetcher {
    @Autowired
    private OpenDataRepo openDataRepo;

    @DgsQuery
    public List<WeatherForecast> showWeatherForecast(@InputArgument String ds) {
        var openDataCwb = JsonConverter.toJsonString(openDataRepo.findByDistrict(ds).get().getWeatherForecast());
        var wList = new LinkedList<WeatherForecast>();
        if (ds != null) {
            wList = JsonConverter.toArrayObject(openDataCwb, new TypeReference<>() {
            });
        }
        return wList;
    }
}
