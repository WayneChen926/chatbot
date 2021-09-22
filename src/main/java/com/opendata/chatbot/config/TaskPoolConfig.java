package com.opendata.chatbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync
@Configuration
public class TaskPoolConfig {

    @Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心線程數10
        executor.setCorePoolSize(10);
        // 最大線程數20
        executor.setMaxPoolSize(20);
        // 緩衝隊列200
        executor.setQueueCapacity(30);
        // 允許線程的空閒時間60秒
        executor.setKeepAliveSeconds(30);
        // 線程池名的前綴
        executor.setThreadNamePrefix("taskExecutor-");
        // 線程池對拒絕任務的處理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
}
