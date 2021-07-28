package com.opendata.chatbot.controller;

import com.opendata.chatbot.entity.Center;
import com.opendata.chatbot.service.OpenDataCWB;
import com.opendata.chatbot.util.JsonConverter;
import com.opendata.chatbot.util.RestTemplateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {
    @Autowired
    Center center;
    @Autowired
    OpenDataCWB openDataCwbImpl;


    @Value("${spring.boot.openCWB.taipeiUrl}")
    private String url;

    @GetMapping("/getFirst")
    public String getFirstApi(){
        String body = RestTemplateUtil.GetNotValueTemplate(url).getBody();
        center = JsonConverter.toObject(body, Center.class);
        return openDataCwbImpl.taipeiCwb(center);
    }
}
