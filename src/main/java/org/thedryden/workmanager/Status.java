package org.thedryden.workmanager;
/***
 * Enum for the various statuses that a Worker can be in
 * @author thedr
 *
 */
public enum Status {
	EMPTY, PENDING, RUNNING, SUCCESS, FAILED, PRECEDENCE_FAILED
}
