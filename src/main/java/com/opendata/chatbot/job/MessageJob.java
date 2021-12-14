package com.opendata.chatbot.job;

import com.opendata.chatbot.job.task.OpenDataTask;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class MessageJob extends QuartzJobBean {

    @Autowired
    private OpenDataTask openDataTaskImpl;

    @Autowired
    private OpenDataTask wakeUpHerokuTaskImpl;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();

        log.info("Set quartz run => {}, Trigger =>{}", jobDataMap.get("name").toString(), jobExecutionContext.getTrigger());

        log.info("**********jobGroupName =>{}", jobExecutionContext.getTrigger().getJobKey().getGroup());

        this.doExecete(jobExecutionContext.getTrigger().getJobKey().getGroup());

        ((OpenDataTask) jobDataMap.get("task")).doRun();
    }

    protected void doExecete(String jobGroupName) {
        long stamp = (new Date()).getTime();

        if (jobGroupName.equals("openDataTaskImpl")) {
            openDataTaskImpl.doRun();
        }else if(jobGroupName.equals("wakeUpHerokuImpl")){
            wakeUpHerokuTaskImpl.doRun();
        }
    }
}
