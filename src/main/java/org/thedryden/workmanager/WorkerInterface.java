package org.thedryden.workmanager;

import java.util.Set;

public interface WorkerInterface {
	public String getThreadName();
	public void setThreadName( String threadName );
	public Status getStatus();
	public void setStatus( Status status );
	public Set<String> getPrecedenceConstraint();
	public void setPrecedenceConstraint( Set<String> precedenceConstraint );
	public int getRetryAttempts();
	public int getRetryWaitSeconds();
	public void setRetry(int retryAttempts, int retryWaitSeconds);
	public void worker() throws Exception;
	public void run();
}
