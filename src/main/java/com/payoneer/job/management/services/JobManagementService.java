package com.payoneer.job.management.services;

import com.payoneer.job.management.entities.AbstractJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.*;

@Service
public class JobManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobManagementService.class);

    private final ScheduledExecutorService scheduledExecutorService;
    private PriorityBlockingQueue<AbstractJob> jobQueue;


//    @Value("${POOL_SIZE}:2")
    private int poolSize = 2;

//    @Value("${INITIAL_QUEUE_SIZE}:8")
    private int initialQueueSize = 10;


    public JobManagementService() {
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
