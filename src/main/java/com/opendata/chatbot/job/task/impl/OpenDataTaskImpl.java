package com.opendata.chatbot.job.task.impl;


import com.opendata.chatbot.job.task.OpenDataTask;
import com.opendata.chatbot.service.OpenDataCwb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OpenDataTaskImpl implements OpenDataTask {

    @Autowired
    private OpenDataCwb openDataCwbImpl;

    @Override
    public void doRun() {
        log.info("=== OpenDataTaskImpl start === ");
        openDataCwbImpl.weatherForecast("新北市");
        openDataCwbImpl.weatherForecast("台北市");
        log.info("=== OpenDataTaskImpl end === ");
    }
}
