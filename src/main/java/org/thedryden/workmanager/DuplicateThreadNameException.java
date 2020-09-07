package org.thedryden.workmanager;

public class DuplicateThreadNameException extends Exception {
	private static final long serialVersionUID = 434825L;
	public DuplicateThreadNameException( String errorMsg ) {
		super(errorMsg);
	}
	public DuplicateThreadNameException() {
		super("No two threads can have the same name.");
	}
}
