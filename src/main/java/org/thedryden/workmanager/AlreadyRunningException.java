package org.thedryden.workmanager;

public class AlreadyRunningException extends Exception {
	private static final long serialVersionUID = 434825L;
	public AlreadyRunningException( String errorMsg ) {
		super(errorMsg);
	}
	public AlreadyRunningException() {
		super("The same pool can't be run at the same time.");
	}
}
