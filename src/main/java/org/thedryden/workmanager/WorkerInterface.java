package org.thedryden.workmanager;

import java.util.Set;

public interface WorkerInterface {
	public String getThreadName();
	public void setThreadName( String threadName );
	public Status getStatus();
	public Set<String> getPrecedenceConstraint();
	public void setPrecedenceConstraint( Set<String> precedenceConstraint );
	public void setStatus( Status status );
	public void worker() throws Exception;
	public void run();
}
