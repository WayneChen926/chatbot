package com.opendata.chatbot.controller;

import com.opendata.chatbot.entity.Center;
import com.opendata.chatbot.util.JsonConverter;
import com.opendata.chatbot.util.RestTemplateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {
    @Autowired
    Center center;
    @GetMapping("/getFirst")
    public String getFirstApi(){
        String url = "https://opendata.cwb.gov.tw/api/v1/rest/datastore/F-D0047-061?Authorization=CWB-C905141A-8E3F-4DA2-BA48-260857AFFA41&format=JSON";
        String body = RestTemplateUtil.GetNotValueTemplate(url).getBody();
        center = JsonConverter.toObject(body, Center.class);
        String json = JsonConverter.toJsonString(center);
        System.out.println(json);
        return body;
    }
}
