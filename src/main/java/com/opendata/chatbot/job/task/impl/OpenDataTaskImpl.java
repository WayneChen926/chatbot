package com.opendata.chatbot.job.task.impl;


import com.opendata.chatbot.job.task.OpenDataTask;
import com.opendata.chatbot.service.OpenDataCwb;
import com.opendata.chatbot.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class OpenDataTaskImpl implements OpenDataTask {

    @Autowired
    private OpenDataCwb openDataCwbImpl;

    @Override
    public void doRun() {
        log.info("=== OpenDataTaskImpl start === ");
        Arrays.stream(Constant.CITY).forEach(city->{
            openDataCwbImpl.weatherForecast(city);
        });
        log.info("=== OpenDataTaskImpl end === ");
    }
}
