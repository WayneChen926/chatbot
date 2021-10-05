package com.opendata.chatbot.job.task;

import org.springframework.stereotype.Service;

@Service
public interface RoutineJobService {

    boolean addRoutineJob(String jobName,String jobGroupName,String jobTime);
}