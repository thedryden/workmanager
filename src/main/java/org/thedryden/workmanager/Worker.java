package org.thedryden.workmanager;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * Default implementation of the WorkerInterface. There really shouldn't be much need to ever not use this abstract class, but if you want feel free to code your own.
 * @author Matthew Dryden
 *
 */
public abstract class Worker implements WorkerInterface {
	protected Set<String> precedenceConstraint;
	protected int retryAttempts;
	protected int retryWaitSeconds;
	protected String threadName;
	protected Logger logger;
	protected Status status;
	
	/***
	 * Creates a new worker with no precedenceConstraint. If you want to have a precedenceConstraint or a threadName that is not the default either overwrite this method, but still call super, or set those values when adding this to a worker pool.
	 */
	public Worker() {
		precedenceConstraint = null;
		retryAttempts = 0;
		retryWaitSeconds = 0;
		threadName = this.getClass().getSimpleName();
		logger = LoggerFactory.getLogger(this.getThreadName());
		status = Status.PENDING;
	}
	/***
	 * Used to set the precedenceConstraint
	 */
	public void setPrecedenceConstraint( Set<String> precedenceConstraint ) {
		this.precedenceConstraint = precedenceConstraint;
	}
	/***
	 * Returns the current precedenceConstraint
	 */
	public Set<String> getPrecedenceConstraint(){
		return precedenceConstraint;
	}
	/***
	 * Adds a precedenceConstraint to the set of precedenceConstraint, creating a new set if necessary
	 * @param precedenceConstraint a thread name you wish to add to the precedenceConstraint
	 * @return this - for method chaining
	 */
	public Worker addPrecedenceConstraint(String precedenceConstraint) {
		if(this.precedenceConstraint == null)
			this.precedenceConstraint = new HashSet<>();
		this.precedenceConstraint.add(precedenceConstraint);
		return this;
	}
	public int getRetryAttempts() {
		return retryAttempts;
	}
	public int getRetryWaitSeconds() {
		return retryWaitSeconds;
	}
	public void setRetry(int retryAttempts, int retryWaitSeconds) {
		this.retryAttempts = retryAttempts;
		this.retryWaitSeconds = retryWaitSeconds;
	}
	/***
	 * Used to set the threadName to something other than the default: this.getClass().getSimpleName()
	 */
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
	/***
	 * Returns the current thread name.
	 */
	public String getThreadName() {
		return threadName;
	}
	/***
	 * Returns the thread name, with the provided string appended to the end
	 * @param append the string to append to the end of the thread name
	 * @return the thread name, with the provided string appended to the end
	 */
	public String getThreadNameAppend(String append) {
		return getThreadName() + append;
	}
	/***
	 * Returns the thread name, with the provided string prepended to the end
	 * @param prepend the string to prepend to the end of the thread name
	 * @return the thread name, with the provided string prepended to the end
	 */
	public String getThreadNamePrepend(String prepend) {
		return prepend + getThreadName();
	}
	/***
	 * Returns the current status of this worker
	 */
	public Status getStatus() {
		return status;
	}
	/***
	 * Sets the current status of this worker. You should never need to call this manually, this is designed to be used by WorkerPool class.
	 */
	public void setStatus(Status status) {
		this.status = status;
	}
	/***
	 * This method is designed to call worker class, while managing both the classes status and error handling and basic logging / timing.
	 */
	public void run() {
		if(status.equals(Status.PENDING)) {
			Timer timer = new Timer().start();
			LoggingTemplate.log(logger, LoggingTemplate.getWorkerStartLevel(), LoggingTemplate.getWorkerStart(), this.getThreadName());
			status = Status.RUNNING;
			int retry = 0;
			boolean done = false;
			while(!done){
				try {
					worker();
					status = Status.SUCCESS;
					done = true;
				} catch (Exception e) {
					retry++;
					if(retry <= retryAttempts) {
						LoggingTemplate.log(logger, LoggingTemplate.getRetryLevel(), LoggingTemplate.getWorkerError(), getThreadName(), e);
						LoggingTemplate.log(logger, LoggingTemplate.getRetryLevel(), LoggingTemplate.getRetry(), "Worker", getThreadName(), retryAttempts, retry, retryWaitSeconds);
						try {Thread.sleep(retryWaitSeconds * 1000);} 
						catch (InterruptedException ie) {}
					} else {
						LoggingTemplate.log(logger, LoggingTemplate.getWorkerErrorLevel(), LoggingTemplate.getWorkerError(), getThreadName(), e);
						status = Status.FAILED;
						break;
					}
				}
			}
			timer.stop();
			LoggingTemplate.log(logger, LoggingTemplate.getWorkerCompleteLevel(), LoggingTemplate.getWorkerComplete(), this.getThreadName(), status, LoggingTemplate.applyTimerToString( timer ));
		}
	}
}
