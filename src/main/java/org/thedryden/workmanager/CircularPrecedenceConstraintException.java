package org.thedryden.workmanager;

public class CircularPrecedenceConstraintException extends Exception {
	private static final long serialVersionUID = 434825L;
	public CircularPrecedenceConstraintException( String errorMsg ) {
		super(errorMsg);
	}
	public CircularPrecedenceConstraintException() {
		super("At least one worker is listed as dependant on a worker it is also dependant on.");
	}
}
