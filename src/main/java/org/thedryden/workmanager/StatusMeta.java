package org.thedryden.workmanager;

import java.util.HashMap;
import java.util.Map;
/***
 * Collection of meta data about statuses used by the WorkerPool class
 * @author thedr
 *
 */
public final class StatusMeta {
	/***
	 * When deciding the outcome a pool, this is used to determine, which status wins. Lower is higher priority.
	 */
	private final static Map<Status,Integer> STATUS_SEVERITY;
	
	static
    { 
		STATUS_SEVERITY = new HashMap<>(); 
		STATUS_SEVERITY.put(Status.FAILED,0);
		STATUS_SEVERITY.put(Status.PRECEDENCE_FAILED,1);
		STATUS_SEVERITY.put(Status.PENDING,2);
		STATUS_SEVERITY.put(Status.RUNNING,4);
		STATUS_SEVERITY.put(Status.SUCCESS,5);
    }
	/***
	 * Takes a status and returns that statuses severity. When deciding the outcome of a pool, status severity is used to determine, which status wins. Lower is higher priority.
	 * @param aStatus the status who's severity you wish to lookup
	 * @return the severity of the status
	 */
	public static int getSeverity( Status aStatus ) {
		return STATUS_SEVERITY.get(aStatus);
	}
	
	/***
	 * If true than the status is considered open, when false it is closed, and should never change.
	 */
	private final static Map<Status,Boolean> STATUS_OPEN;
	
	static
    { 
		STATUS_OPEN = new HashMap<>(); 
		STATUS_OPEN.put(Status.FAILED,false);
		STATUS_OPEN.put(Status.PRECEDENCE_FAILED,false);
		STATUS_OPEN.put(Status.PENDING,true);
		STATUS_OPEN.put(Status.RUNNING,true);
		STATUS_OPEN.put(Status.SUCCESS,false);
    }
	/***
	 * Takes a status and returns true if that status is considered open. If closed (false) than the status should never change.
	 * @param aStatus The status you wish to check
	 * @return true if status is open, false if closed
	 */
	public static boolean isOpen( Status aStatus ) {
		return STATUS_OPEN.get(aStatus);
	}
	
	/***
	 * If true than the status is considered failed.
	 */
	private final static Map<Status,Boolean> STATUS_FAILED;
	
	static
    { 
		STATUS_FAILED = new HashMap<>(); 
		STATUS_FAILED.put(Status.FAILED,true);
		STATUS_FAILED.put(Status.PRECEDENCE_FAILED,true);
		STATUS_FAILED.put(Status.PENDING,false);
		STATUS_FAILED.put(Status.RUNNING,false);
		STATUS_FAILED.put(Status.SUCCESS,false);
    }
	/***
	 * Takes a status and returns true if the status means a failure, false if not
	 * @param aStatus the status to check
	 * @return true if the status means a failure, false if not
	 */
	public static boolean isFailed(Status aStatus) {
		return STATUS_FAILED.get(aStatus);
	}
}
