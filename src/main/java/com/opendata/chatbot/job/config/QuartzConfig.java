package com.opendata.chatbot.job.config;

import com.opendata.chatbot.job.PushMessageJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {
    @Bean
    public JobDetail pushQuartz() {
        return JobBuilder.newJob(PushMessageJob.class).withIdentity("pushTask").storeDurably().build();
    }

    @Bean
    public Trigger pushQuartzTrigger() {
        //5秒执行一次
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(60*60*12)
                .repeatForever();
        return TriggerBuilder.newTrigger().forJob(pushQuartz())
                .withIdentity("pushTask")
                .withSchedule(scheduleBuilder)
                .build();
    }
}
