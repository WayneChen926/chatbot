package com.opendata.chatbot.controller;

import com.opendata.chatbot.dao.User;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class SendMessageController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/put_message")
    public String putMessage(@RequestParam(name = "id") String id) {
        rabbitTemplate.convertAndSend("tpu.queue", new User(id, LocalDateTime.now(), "message"));
        return "this is quick demo for Spring Boot!";
    }
}