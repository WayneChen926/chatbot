package com.opendata.chatbot.job.task.impl;


import com.opendata.chatbot.job.task.OpenDataTask;
import com.opendata.chatbot.repository.OpenDataRepo;
import com.opendata.chatbot.service.OpenDataCwb;
import com.opendata.chatbot.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class OpenDataTaskImpl implements OpenDataTask {

    @Autowired
    private OpenDataCwb openDataCwbImpl;

    @Autowired
    private OpenDataRepo openDataRepo;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void doRun() {
        log.info("=== OpenDataTaskImpl start === ");
        Arrays.stream(Constant.CITY).forEach(city -> {
            try {
                Thread.sleep(2000);
                rabbitTemplate.convertAndSend("tpu.queue", city,
                        correlationData -> {
                            correlationData.getMessageProperties().setDelay(2000);
                            return correlationData;
                        });
                openDataCwbImpl.weatherForecast(city);
            } catch (Exception e) {
                log.error("RabbitTemplate doRun :{}", e.getMessage());
            }
        });

        // 寫入 Redis
        openDataRepo.findAll().forEach(weatherForecastDto -> {
            redisTemplate.delete(weatherForecastDto.getDistrict());
            redisTemplate.opsForValue().set(weatherForecastDto.getDistrict(), openDataRepo.findByDistrict(weatherForecastDto.getDistrict()));
        });

        log.info("=== OpenDataTaskImpl end === ");
    }
}
