package com.opendata.chatbot.job.task.impl;


import com.opendata.chatbot.job.task.PushTask;
import com.opendata.chatbot.service.LineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PushMessageWeatherForecast implements PushTask {

    @Autowired
    private LineService lineService;

    @Override
    public void doRun() {

    }
}
