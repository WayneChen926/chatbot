package com.opendata.chatbot.job.task.impl;

import com.opendata.chatbot.job.task.OpenDataTask;
import com.opendata.chatbot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WakeUpHerokuTaskImpl implements OpenDataTask {

    @Autowired
    private UserService userServiceImpl;

    @Override
    public void doRun() {
        log.info("=== WakeUpHerokuTaskImpl start === ");
        userServiceImpl.getAllUsers();
        log.info("=== WakeUpHerokuTaskImpl end === ");
    }
}
