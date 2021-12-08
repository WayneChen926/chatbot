package com.opendata.chatbot.listener;

import com.opendata.chatbot.dao.User;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ReceiveMessageListener {

    /**
     * 監聽Queue中是否有資料，若有資料則進行消費。
     *
     * @param user
     */
    @RabbitListener(queues = {"tpu.queue"})
    public void receive(User user) {
        System.out.println("receive message from queue:" + user.toString());
    }

}