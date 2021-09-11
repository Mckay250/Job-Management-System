package com.payoneer.job.management;

import com.payoneer.job.management.entities.AbstractJob;
import com.payoneer.job.management.enums.JobPriority;
import com.payoneer.job.management.enums.JobState;
import com.payoneer.job.management.jobs.FailingEmailJob;
import com.payoneer.job.management.jobs.SendEmailJob;
import com.payoneer.job.management.services.JobManagementService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class JobManagementServiceTests {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobManagementServiceTests.class);

	@Autowired
	JobManagementService jobManagementService;

	@Test
	public void whenMultiplePriorityJobsAreQueued_thenHighestPriorityJobArePickedFirst() throws InterruptedException {
		AbstractJob emailJob1 = new SendEmailJob("Job1", 0L, TimeUnit.SECONDS, JobPriority.LOW);
		AbstractJob emailJob2 = new SendEmailJob("Job2", 1L, TimeUnit.SECONDS, JobPriority.HIGH);
		AbstractJob emailJob3 = new SendEmailJob("Job3", 0L, TimeUnit.SECONDS,  JobPriority.LOW);
		AbstractJob emailJob4 = new SendEmailJob("Job4", 1L, TimeUnit.SECONDS,  JobPriority.HIGH);
		AbstractJob emailJob5 = new SendEmailJob("Job5", 0L, TimeUnit.SECONDS,  JobPriority.LOW);
		AbstractJob emailJob6 = new SendEmailJob("Job6", 0L, TimeUnit.SECONDS,  JobPriority.LOW);

//		Jobs should be in the QUEUED state on initialization
		assertEquals(JobState.QUEUED, emailJob1.getJobState());
		assertEquals(JobState.QUEUED, emailJob2.getJobState());
		assertEquals(JobState.QUEUED, emailJob3.getJobState());
		assertEquals(JobState.QUEUED, emailJob4.getJobState());
		assertEquals(JobState.QUEUED, emailJob5.getJobState());
		assertEquals(JobState.QUEUED, emailJob6.getJobState());

		jobManagementService.scheduleJob(emailJob1);
		jobManagementService.scheduleJob(emailJob2);
		jobManagementService.scheduleJob(emailJob3);
		jobManagementService.scheduleJob(emailJob4);
		jobManagementService.scheduleJob(emailJob5);
		jobManagementService.scheduleJob(emailJob6);

		LOGGER.info("main Thread will now sleep for 20 seconds to allow for execution logging");
		Thread.sleep(20000);

		assertEquals(JobState.SUCCESS, emailJob1.getJobState());
		assertEquals(JobState.SUCCESS, emailJob2.getJobState());
		assertEquals(JobState.SUCCESS, emailJob3.getJobState());
		assertEquals(JobState.SUCCESS, emailJob4.getJobState());
		assertEquals(JobState.SUCCESS, emailJob5.getJobState());
		assertEquals(JobState.SUCCESS, emailJob6.getJobState());

	}

	@Test
	public void whenAJobFails_thenTheStatusShouldBeFailedAfterExecution() throws InterruptedException {
		AbstractJob emailJob1 = new FailingEmailJob("failingEmailJob", 0L, TimeUnit.SECONDS, JobPriority.LOW);
		AbstractJob emailJob2 = new SendEmailJob("successfulEmailJob", 0L, TimeUnit.SECONDS, JobPriority.LOW);


		jobManagementService.scheduleJob(emailJob1);
		jobManagementService.scheduleJob(emailJob2);

		LOGGER.info("main Thread will now sleep for 20 seconds to allow for execution logging");
		Thread.sleep(10000);

		assertEquals(JobState.FAILED, emailJob1.getJobState());
		assertEquals(JobState.SUCCESS, emailJob2.getJobState());
	}


	@Test
	public void whenADelayIsGreaterThanZero_thenTheJobShouldOnlyExecuteAfterTheSetDelay() throws InterruptedException {
		AbstractJob delayedJob = new SendEmailJob("delayedJob", 7L, TimeUnit.SECONDS, JobPriority.LOW);
		AbstractJob immediateJob = new SendEmailJob("immediateJob", 0L, TimeUnit.SECONDS, JobPriority.LOW);

		jobManagementService.scheduleJob(delayedJob);
		jobManagementService.scheduleJob(immediateJob);

		Thread.sleep(3000);

		assertEquals(JobState.QUEUED, delayedJob.getJobState());
		assertEquals(JobState.SUCCESS, immediateJob.getJobState());


		LOGGER.info("main Thread will now sleep for 10 seconds to allow completion of all jobs");
		Thread.sleep(10000);

		assertEquals(JobState.SUCCESS, delayedJob.getJobState());
		assertEquals(JobState.SUCCESS, immediateJob.getJobState());
	}


}
