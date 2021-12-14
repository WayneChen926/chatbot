package com.opendata.chatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ChatBotApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ChatBotApplication.class, args);
    }

    // 外部tomcat 佈署設定檔
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ChatBotApplication.class);
    }
}
