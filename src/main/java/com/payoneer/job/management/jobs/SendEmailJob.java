package com.payoneer.job.management.jobs;

import com.payoneer.job.management.entities.AbstractJob;
import com.payoneer.job.management.enums.JobPriority;
import com.payoneer.job.management.enums.JobState;
import com.payoneer.job.management.exceptions.SendEmailJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class SendEmailJob extends AbstractJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendEmailJob.class);


    public SendEmailJob(String jobName, long delay, TimeUnit timeUnit, JobPriority jobPriority) {
        super(jobName, delay, timeUnit, jobPriority);
    }

    @Override
    public SendEmailJob call() {
        try {
            setJobState(JobState.RUNNING);
            performJob(); //Transactional execution of job
            setJobState(JobState.SUCCESS);
            LOGGER.info("{} completed successfully", getJobName());
        } catch (SendEmailJobException e) {
            setJobState(JobState.FAILED);
            LOGGER.info("{} failed to complete", getJobName());
            setErrorMessage(e.getLocalizedMessage());
        }
        return this;
    }


    private void performJob() throws SendEmailJobException {
        try {
            LOGGER.info("Job with Name: {}, Priority: {}, and DelayTime: {} in {} is being executed!",
                    getJobName(),
                    getPriority(),
                    getTimeDelay(),
                    getTimeDelayUnit().toString());
            Thread.sleep(1000); // introducing delay to simulate execution time
        } catch (Exception e) {
            throw new SendEmailJobException(e);
        }
    }

}
