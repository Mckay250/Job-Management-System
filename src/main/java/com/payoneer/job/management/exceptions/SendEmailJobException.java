package com.payoneer.job.management.exceptions;

public class SendEmailJobException extends Exception {

	public SendEmailJobException(Throwable error) {
		super(error.getLocalizedMessage(), error);
	}
}
