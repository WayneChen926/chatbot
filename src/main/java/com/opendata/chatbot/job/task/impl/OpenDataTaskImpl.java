package com.opendata.chatbot.job.task.impl;


import com.opendata.chatbot.job.task.OpenDataTask;
import com.opendata.chatbot.repository.OpenDataRepo;
import com.opendata.chatbot.service.OpenDataCwb;
import com.opendata.chatbot.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class OpenDataTaskImpl implements OpenDataTask {

    @Autowired
    private OpenDataCwb openDataCwbImpl;

    @Autowired
    private OpenDataRepo openDataRepo;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void doRun() {
        log.info("=== OpenDataTaskImpl start === ");
        Arrays.stream(Constant.CITY).forEach(city -> {
            try {
                Thread.sleep(2000);
                openDataCwbImpl.weatherForecast(city);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        openDataRepo.findAll().forEach(weatherForecastDto -> {
            var weatherForecastDtoList = openDataRepo.findByDistrict(weatherForecastDto.getDistrict());
            redisTemplate.delete(weatherForecastDto.getDistrict());
            redisTemplate.opsForValue().set(weatherForecastDto.getDistrict(), weatherForecastDtoList);
        });

        log.info("=== OpenDataTaskImpl end === ");
    }
}
