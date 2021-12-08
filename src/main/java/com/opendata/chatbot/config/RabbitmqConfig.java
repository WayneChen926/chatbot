package com.opendata.chatbot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitmqConfig {
    /**
     * 將自定義的消息類序列化成json格式，再轉成byte構造 Message，在接收消息時，會將接收到的 Message 再反序列化成自定義的類。
     *
     * @param objectMapper
     * @return
     */
    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    /**
     * create Rabbit Queue
     *
     * @return
     */
    @Bean
    public Queue tpuQueue() {
        return new Queue("tpu.queue");
    }
}
