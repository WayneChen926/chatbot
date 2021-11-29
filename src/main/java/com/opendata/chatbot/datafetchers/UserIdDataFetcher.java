package com.opendata.chatbot.datafetchers;


import com.fasterxml.jackson.core.type.TypeReference;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import com.opendata.chatbot.dao.User;
import com.opendata.chatbot.entity.WeatherForecast;
import com.opendata.chatbot.repository.OpenDataRepo;
import com.opendata.chatbot.service.UserService;
import com.opendata.chatbot.util.JsonConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@DgsComponent
@Slf4j
public class UserIdDataFetcher {
    @Autowired
    private OpenDataRepo openDataRepo;

    @Autowired
    private UserService userServiceImpl;

    @DgsQuery
    public List<WeatherForecast> showWeatherForecast(@InputArgument String ds) {
        var low = openDataRepo.findByDistrict(ds);
        log.info("low :{}", low);
        AtomicReference<List<WeatherForecast>> s = new AtomicReference<>();
        low.forEach(openData -> {
            s.set(openData.get().getWeatherForecast());
        });
        return s.get();
    }

    @DgsMutation
    @Transactional(rollbackFor = Exception.class)
    public User createPromotion(@Validated @InputArgument("input") User user) {
        return userServiceImpl.saveUser(user);
    }
}
