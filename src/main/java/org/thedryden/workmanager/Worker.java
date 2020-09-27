package org.thedryden.workmanager;

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
	protected String threadName;
	protected Logger logger;
	protected Status status;
	
	/***
	 * Creates a new worker with no precedenceConstraint. If you want to have a precedenceConstraint or a threadName that is not the default either overwrite this method, but still call super, or set those values when adding this to a worker pool.
	 */
	public Worker() {
		precedenceConstraint = null;
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
			try {
				worker();
				status = Status.SUCCESS;
			} catch (Exception e) {
				LoggingTemplate.log(logger, LoggingTemplate.getWorkerErrorLevel(), LoggingTemplate.getWorkerError(), getThreadName(), e);
				status = Status.FAILED;
			}
			timer.stop();
			LoggingTemplate.log(logger, LoggingTemplate.getWorkerCompleteLevel(), LoggingTemplate.getWorkerComplete(), this.getThreadName(), status, timer.toString());
		}
	}
}
