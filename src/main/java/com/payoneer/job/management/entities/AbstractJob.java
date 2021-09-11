package com.payoneer.job.management.entities;

import com.payoneer.job.management.enums.JobPriority;
import com.payoneer.job.management.enums.JobState;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public abstract class AbstractJob implements Callable<AbstractJob> {

    private final String jobName;
    private long timeDelay;
    private TimeUnit timeDelayUnit;
    private JobState jobState;
    private JobPriority priority;
    private String errorMessage;

    public AbstractJob(String jobName, long delay, TimeUnit timeDelayUnit, JobPriority jobPriority) {
        this.jobName = jobName;
        this.jobState = JobState.QUEUED;
        this.priority = jobPriority;
//        this.timeDelay = this.priority == JobPriority.HIGH ? 0 : delay; // time delay is ignored/set to zero for jobs with high priority
        this.timeDelay = delay;
        this.timeDelayUnit = timeDelayUnit;
    }

    @Override
    public String toString() {
        return "AbstractJob{" +
                ", jobName='" + jobName + '\'' +
                ", timeDelay=" + timeDelay +
                ", timeDelayUnit=" + timeDelayUnit +
                ", jobState=" + jobState +
                ", priority=" + priority +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
