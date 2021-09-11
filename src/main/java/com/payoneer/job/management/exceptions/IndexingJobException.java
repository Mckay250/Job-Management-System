package com.payoneer.job.management.exceptions;

public class IndexingJobException extends Exception {

	private static final long serialVersionUID = -4254090464474477224L;

	public IndexingJobException(Throwable error) {
		super(error.getLocalizedMessage(), error);
	}
}
