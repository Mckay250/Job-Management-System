package com.payoneer.job.management.exceptions;

public class DataLoadJobException extends Exception {

	private static final long serialVersionUID = 1399080348183630735L;

	public DataLoadJobException(Throwable error) {
		super(error.getLocalizedMessage(), error);
	}
}
