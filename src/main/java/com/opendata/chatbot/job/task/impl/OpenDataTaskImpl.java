package com.opendata.chatbot.job.task.impl;


import com.opendata.chatbot.job.task.OpenDataTask;
import com.opendata.chatbot.repository.OpenDataRepo;
import com.opendata.chatbot.service.OpenDataCwb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
        openDataCwbImpl.cityCwb();

        // 寫入 Redis
        openDataRepo.findAll().forEach(weatherForecastDto -> {
            redisTemplate.delete(weatherForecastDto.getDistrict());
            redisTemplate.opsForValue().set(weatherForecastDto.getDistrict(), openDataRepo.findByDistrict(weatherForecastDto.getDistrict()));
        });

        log.info("=== OpenDataTaskImpl end === ");
    }
}
