package com.opendata.chatbot.job;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Component
public class PushMessageJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Trigger trigger = context.getTrigger();
        JobDetail jobDetail = context.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        this.doExecete(context.getTrigger().getJobKey().getGroup());
    }

    protected long doExecete(String jobGroupName){
        long stamp = (new Date()).getTime();
        return stamp;
    }
}
