package com.opendata.chatbot.job.task.impl;

import com.opendata.chatbot.job.task.OpenDataTask;
import com.opendata.chatbot.service.AesECB;
import com.opendata.chatbot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Base64;

@Slf4j
@Service
public class WakeUpHerokuTaskImpl implements OpenDataTask {

    @Autowired
    private UserService userServiceImpl;

    @Autowired
    private AesECB aesECBImpl;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void doRun() {
        log.info("=== WakeUpHerokuTaskImpl start === ");
        var url = aesECBImpl.aesDecrypt("Qn991V83Zh2Rbg4soINVw0isnJmkaqhDzwI80r9rZ68C3x2QyuN3f8/LupiQpqdLYvTUXBBPk5m7AbgQskRl+Q==");
        var data = restTemplate.getForEntity(URI.create(new String(Base64.getDecoder().decode(url))), String.class).getBody();
        log.info("url data :{}", data);
        log.info("=== WakeUpHerokuTaskImpl end === ");
    }
}
