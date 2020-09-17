package org.thedryden.workmanager;

/***
 * Enum that list the 5 levels of logging available. There is also a sixth level: off. If this is choosen as the logging level for a message than nothing will be sent to the log for that message.
 * @author thedr
 *
 */
public enum LoggingLevel {
	trace, debug, info, warn, error, off
}