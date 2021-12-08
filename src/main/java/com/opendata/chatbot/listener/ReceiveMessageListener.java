package com.opendata.chatbot.listener;

import com.opendata.chatbot.dao.User;
import com.opendata.chatbot.entity.Location;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

@Component
@RabbitListener(queues = {"tpu.queue"})
@Slf4j
public class ReceiveMessageListener {

    public static ArrayBlockingQueue<Serializable> queue = new ArrayBlockingQueue<>(200);

    public void receive(String body) {
        System.out.println("receive message from queue:" + body);
    }

    @RabbitHandler
    public void onMessage(String city) {
        try {
            //将该对象存入阻塞队列中
            queue.put(city);
        } catch (InterruptedException e) {
            log.error("插入列隊異常 :{}", e.getMessage());
        }
    }

}