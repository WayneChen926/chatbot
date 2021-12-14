package com.opendata.chatbot.config;

import com.opendata.chatbot.entity.QuartzInfo;
import com.opendata.chatbot.job.task.RoutineJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InitQuartz implements CommandLineRunner {
    @Autowired
    RoutineJobService routineJobService;

    @Autowired
    QuartzInfo quartzInfo;

    @Value("${spring.quartz.cron}")
    private String cron;
    @Value("${spring.quartz.jobName}")
    private String jobName;
    @Value("${spring.quartz.jobGroupName}")
    private String jobGroupName;

    @Override
    public void run(String... args) throws Exception {
        log.trace("========= run init InitQuartz ======");

        quartzInfo.setJobName(jobName);
        quartzInfo.setJobGroupName(jobGroupName);
        quartzInfo.setCron(cron);


        routineJobService.addRoutineJob(quartzInfo.getJobName(), quartzInfo.getJobGroupName(),
                quartzInfo.getCron());

        routineJobService.addRoutineJob(quartzInfo.getJobName(), "WakeUpHerokuTaskImpl", "0 */25 6-23 * * ?");

    }
}
