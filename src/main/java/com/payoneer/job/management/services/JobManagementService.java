package com.payoneer.job.management.services;

import com.payoneer.job.management.entities.AbstractJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.*;

@Service
public class JobManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobManagementService.class);

    private ScheduledExecutorService scheduledExecutorService;
    private PriorityBlockingQueue<AbstractJob> jobQueue;


    @Value("${pool.size}")
    private int poolSize;

    @Value("${initial.queue.size}")
    private int initialQueueSize;


    @PostConstruct
    public void init() {
        scheduledExecutorService = Executors.newScheduledThreadPool(poolSize);
        jobQueue = new PriorityBlockingQueue<>(
                initialQueueSize,
                Comparator.comparing(AbstractJob::getPriority));

        scheduledExecutorService.execute(() -> {
            while (true) {
                try {
                    AbstractJob job = jobQueue.take();
                    LOGGER.info("Scheduling "+ job.getJobName());
                    scheduledExecutorService.schedule(job, job.getTimeDelay(), job.getTimeDelayUnit());
                } catch (Exception e) {
                    System.out.println("Exception occurred: " + e.getLocalizedMessage());
                    break;
                }
            }
        });
    }

    public void scheduleJob(AbstractJob job) {
        jobQueue.add(job);
    }

    @PreDestroy
    private void cleanUp() {
        scheduledExecutorService.shutdown();
        jobQueue = null;
    }
}
