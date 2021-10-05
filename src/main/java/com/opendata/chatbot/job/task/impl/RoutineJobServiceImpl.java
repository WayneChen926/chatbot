package com.opendata.chatbot.job.task.impl;

import com.opendata.chatbot.job.MessageJob;
import com.opendata.chatbot.job.task.OpenDataTask;
import com.opendata.chatbot.job.task.RoutineJobService;
import com.opendata.chatbot.util.QuartzUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Slf4j
@Service
public class RoutineJobServiceImpl implements RoutineJobService, BeanPostProcessor {

    private static final List<OpenDataTask> openDataTaskList = new ArrayList<>();

    @Autowired
    QuartzUtils quartzUtils;

    @Override
    public boolean addRoutineJob(String jobName, String jobGroupName, String jobTime) {

        log.info("addRoutineJob -> jobName :{}, jobGroupName: {}, jobTime:{}", jobName,
                jobGroupName, jobTime);

        Map<String, Object> jobData = new HashMap<>();
        jobData.put("name", jobName);

        Optional<OpenDataTask> crawlTaskOpt = this.setTask(jobGroupName);

        if (!crawlTaskOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task no exist");
        }

        jobData.put("task", crawlTaskOpt.get());

        quartzUtils.addJob(MessageJob.class, jobName, jobGroupName, jobTime, jobData);

        log.info("add job finish ,jobData -> {} ", jobData);
        return true;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof OpenDataTask) {
            openDataTaskList.add((OpenDataTask)bean);
        }
        return bean;
    }

    private Optional<OpenDataTask> setTask(String jobGroupName) {
        Optional<OpenDataTask> task = openDataTaskList.stream()
                .filter(openDataJobTask -> ClassUtils.getUserClass(openDataJobTask).getSimpleName().equals(jobGroupName))
                .findFirst();
        return task;
    }

}