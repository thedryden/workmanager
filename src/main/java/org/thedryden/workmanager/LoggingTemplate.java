package org.thedryden.workmanager;

import org.slf4j.Logger;
/***
 * The purpose of this package is to allow you to customize how the logging messages and their levels.
 * You will be able to change both the contents of the message and the logging level of the message, but you will not be able to change the variables passed to that message, or the order those variables appear.
 * For each message the variable that will be passed into that message will be listed in the description of the setter.
 * This class contains a collection of static values.
 * Since these values are all static, a change to any of the values will change how logging works for the all uses of this package.
 * @author Matthew Dryden
 *
 */
public class LoggingTemplate {
	protected static String workerStart = "Starting worker: {}";
	protected static LoggingLevel workerStartLevel = LoggingLevel.info;
	protected static String workerComplete = "Worker {} Completed with status: {}. It ran for {}";
	protected static LoggingLevel workerCompleteLevel = LoggingLevel.info;
	protected static String workerError = "An error occured while running the thread: {}";
	protected static LoggingLevel workerErrorLevel = LoggingLevel.error;
	protected static String poolStartAllPools = "Starting all pools. There are {} pools.";
	protected static LoggingLevel poolStartAllPollsLevel = LoggingLevel.info;
	protected static String poolStopingNextPoolOnError = "Stopping executiong because pool: {}  did not return success.";
	protected static LoggingLevel poolStopingNextPoolOnErrorLevel = LoggingLevel.warn;
	protected static LoggingLevel poolStartLevel = LoggingLevel.info;
	protected static String poolStartFinished = "Finished running all pools. It took {}.";
	protected static LoggingLevel poolFinishedStartLevel = LoggingLevel.info;
	protected static String poolStopNextWorkerOnError = "Since stop all running on failure is set to true, and at least one thread in the pool has failed, the pool {} will not start any more threads.";
	protected static LoggingLevel poolStopNextWorkerOnErrorLevel = LoggingLevel.info;
	protected static String poolExitOnError = "Pool {} failed so this application is exiting with status code -1.";
	protected static LoggingLevel poolExitOnErrorLevel = LoggingLevel.error;
	/***
	 * Takes an existing logger and allows you to select a LoggingLevel at run time, rather than having to hard code your selection.
	 * @param logger the logger you wish to call
	 * @param level The levle you wish to send the message at
	 * @param msg the message string to be logged
	 */
	public static void log( Logger logger, LoggingLevel level, String msg) {
		if(LoggingLevel.debug.equals(level))
			logger.debug(msg);
		else if (LoggingLevel.error.equals(level))
			logger.error(msg);
		else if (LoggingLevel.info.equals(level))
			logger.info(msg);
		else if (LoggingLevel.trace.equals(level))
			logger.trace(msg);
		else if(LoggingLevel.warn.equals(level))
			logger.warn(msg);
	}
	/***
	 * Takes an existing logger and allows you to select a LoggingLevel at run time, rather than having to hard code your selection.
	 * @param logger the logger you wish to call
	 * @param level The level you wish to send the message at
     * @param format the format string
     * @param arg    the argument
	 */
	public static void log( Logger logger, LoggingLevel level, String format, Object arg) {
		if(LoggingLevel.debug.equals(level))
			logger.debug(format, arg);
		else if (LoggingLevel.error.equals(level))
			logger.error(format, arg);
		else if (LoggingLevel.info.equals(level))
			logger.info(format, arg);
		else if (LoggingLevel.trace.equals(level))
			logger.trace(format, arg);
		else if(LoggingLevel.warn.equals(level))
			logger.warn(format, arg);
	}
	/***
	 * Takes an existing logger and allows you to select a LoggingLevel at run time, rather than having to hard code your selection.
	 * @param logger the logger you wish to call
	 * @param level The level you wish to send the message at
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
	 */
	public static void log( Logger logger, LoggingLevel level, String format,  Object arg1, Object arg2) {
		if(LoggingLevel.debug.equals(level))
			logger.debug(format, arg1, arg2);
		else if (LoggingLevel.error.equals(level))
			logger.error(format, arg1, arg2);
		else if (LoggingLevel.info.equals(level))
			logger.info(format, arg1, arg2);
		else if (LoggingLevel.trace.equals(level))
			logger.trace(format, arg1, arg2);
		else if(LoggingLevel.warn.equals(level))
			logger.warn(format, arg1, arg2);
	}
	/***
	 * Takes an existing logger and allows you to select a LoggingLevel at run time, rather than having to hard code your selection.
	 * @param logger the logger you wish to call
	 * @param level The level you wish to send the message at
     * @param format    the format string
     * @param arguments a list of 3 or more argument
	 */
	public static void log( Logger logger, LoggingLevel level, String format, Object... arguments ) {
		if(LoggingLevel.debug.equals(level))
			logger.debug(format, arguments);
		else if (LoggingLevel.error.equals(level))
			logger.error(format, arguments);
		else if (LoggingLevel.info.equals(level))
			logger.info(format, arguments);
		else if (LoggingLevel.trace.equals(level))
			logger.trace(format, arguments);
		else if(LoggingLevel.warn.equals(level))
			logger.warn(format, arguments);
	}
	/***
	 * Returns the current message for that will display when a worker is started.
	 * @return the current message for that will display when a worker is started.
	 */
	public static String getWorkerStart() {
		return workerStart;
	}
	/***
	 * Used to override the default message that will display when a worker is started. Expects one argument that will be the name of the thread.
	 * @param workerStart the new message to display when a worker is started.
	 */
	public static void setWorkerStart(String workerStart) {
		LoggingTemplate.workerStart = workerStart;
	}
	/***
	 * Returns the logging level for when a worker is started.
	 * @return the logging level for when a worker is started.
	 */
	public static LoggingLevel getWorkerStartLevel() {
		return workerStartLevel;
	}
	/***
	 * Used to overwrite the logging level for when a worker is started.
	 * @param workerStartLevel the new logging level for when a worker is started.
	 */
	public static void setWorkerStartLevel(LoggingLevel workerStartLevel) {
		LoggingTemplate.workerStartLevel = workerStartLevel;
	}
	/***
	 * Returns the current message that will display when a worker completes its work.
	 * @return the current message that will display when a worker completes its work.
	 */
	public static String getWorkerComplete() {
		return workerComplete;
	}
	/***
	 * Used to override the default message that will display when a worker completes. Expects three arguments 1) the name of the thread 2) The status of the worker 3) A string representing how long the worker was running.
	 * @param workerComplete the new message that will display when a worker completes.
	 */
	public static void setWorkerComplete(String workerComplete) {
		LoggingTemplate.workerComplete = workerComplete;
	}
	/***
	 * Returns the logging level for when a worker completes its work.
	 * @return the logging level for when a worker completes its work.
	 */
	public static LoggingLevel getWorkerCompleteLevel() {
		return workerCompleteLevel;
	}
	/***
	 * Used to overwrite the logging level for when a worker completes its work.
	 * @param workerCompleteLevel the new logging level for when a worker completes its work.
	 */
	public static void setWorkerCompleteLevel(LoggingLevel workerCompleteLevel) {
		LoggingTemplate.workerCompleteLevel = workerCompleteLevel;
	}
	/***
	 * Returns the current message that will display when a worker is stopped by an error.
	 * @return the current message that will display when a worker is stopped by an error.
	 */
	public static String getWorkerError() {
		return workerError;
	}
	/***
	 * Used to override the default message that will display when a worker stops with an error. Expects two arguments 1) the name of the thread 2) The exception the worker encountered.
	 * @param workerComplete
	 */
	public static void setWorkerError(String workerError) {
		LoggingTemplate.workerError = workerError;
	}
	/***
	 * Returns the logging level for when a worker is stopped by an error.
	 * @return the logging level for when a worker is stopped by an error.
	 */
	public static LoggingLevel getWorkerErrorLevel() {
		return workerErrorLevel;
	}
	/***
	 * Used to overwrite the logging level for when a worker is stopped by an error.
	 * @param workerCompleteLevel the new logging level for when a worker is stopped by an error.
	 */
	public static void setWorkerErrorLevel(LoggingLevel workerErrorLevel) {
		LoggingTemplate.workerErrorLevel = workerErrorLevel;
	}
	/***
	 * Returns the current message that will display when WorkerPool starts all pools.
	 * @return the current message that will display when WorkerPool starts all pools.
	 */
	public static String getPoolStartAllPools() {
		return poolStartAllPools;
	}
	/***
	 * Used to override the default message that will display when WorkerPool starts all pools. Expects one argument, the number of pools.
	 * @param workerComplete
	 */
	public static void setPoolStartAllPools(String poolStartAllPools) {
		LoggingTemplate.poolStartAllPools = poolStartAllPools;
	}
	/***
	 * Returns the logging level for when WorkerPool starts all pools.
	 * @return the logging level for when WorkerPool starts all pools.
	 */
	public static LoggingLevel getPoolStartAllPollsLevel() {
		return poolStartAllPollsLevel;
	}
	/***
	 * Used to overwrite the logging level for when WorkerPool starts all pools.
	 * @param workerCompleteLevel the new logging level for when WorkerPool starts all pools.
	 */
	public static void setPoolStartAllPollsLevel(LoggingLevel poolStartAllPollsLevel) {
		LoggingTemplate.poolStartAllPollsLevel = poolStartAllPollsLevel;
	}
	/***
	 * Returns the current message that will display when WorkerPool stops the next pool from starting because the previous pool errored.
	 * @return the current message that will display when WorkerPool stops the next pool from starting because the previous pool errored.
	 */
	public static String getPoolStopingNextPoolOnError() {
		return poolStopingNextPoolOnError;
	}
	/***
	 * Used to override the default message that will display when WorkerPool stops the next pool from starting because the previous pool errored. Expects one argument, the name of the pool that failed.
	 * @param workerComplete the new message that will display when WorkerPool stops the next pool from starting because the previous pool errored.
	 */
	public static void setPoolStopingNextPoolOnError(String poolStopingAllOnError) {
		LoggingTemplate.poolStopingNextPoolOnError = poolStopingAllOnError;
	}
	/***
	 * Returns the logging level for when WorkerPool stops the next pool from starting because the previous pool errored.
	 * @return the logging level for when WorkerPool stops the next pool from starting because the previous pool errored.
	 */
	public static LoggingLevel getPoolStopingNextPoolOnErrorLevel() {
		return poolStopingNextPoolOnErrorLevel;
	}
	/***
	 * Used to overwrite the default logging level when WorkerPool stops new threads from starting because the previous pool errored.
	 * @param workerCompleteLevel the new logging level when WorkerPool stops new threads from starting because the previous pool errored.
	 */
	public static void setPoolStopingNextPoolOnErrorLevel(LoggingLevel poolStopingAllOnErrorLevel) {
		LoggingTemplate.poolStopingNextPoolOnErrorLevel = poolStopingAllOnErrorLevel;
	}
	/***
	 * Returns the logging level when starting a pool
	 * @return the logging level when starting a pool.
	 */
	public static LoggingLevel getPoolStartLevel() {
		return poolStartLevel;
	}
	/***
	 * Used to overwrite the default logging level for starting a pool. Note: the message itself can't be overwritten.
	 * @param workerCompleteLevel the new logging level for starting a pool.
	 */
	public static void setPoolStartLevel(LoggingLevel poolStartLevel) {
		LoggingTemplate.poolStartLevel = poolStartLevel;
	}
	/***
	 * Returns the current message that will display when all pools complete after calling start
	 * @return the current message that will display when all pools complete after calling start
	 */
	public static String getPoolStartFinished() {
		return poolStartFinished;
	}
	/***
	 * Used to override the default message that will display when all pools complete after calling start. Takes one parameter, the time it took run all pools.
	 * @param workerComplete the new message that will display when all pools complete after calling start.
	 */
	public static void setPoolStartFinished(String poolStartFinished) {
		LoggingTemplate.poolStartFinished = poolStartFinished;
	}
	/***
	 * Returns the logging level for when all pools complete after calling start.
	 * @return the logging level for when all pools complete after calling start.
	 */
	public static LoggingLevel getPoolFinishedStartLevel() {
		return poolFinishedStartLevel;
	}
	/***
	 * Used to overwrite the default logging level when all pools complete after calling start.
	 * @param workerCompleteLevel the new logging level when all pools complete after calling start.
	 */
	public static void setPoolFinishedStartLevel(LoggingLevel poolFinishedStartLevel) {
		LoggingTemplate.poolFinishedStartLevel = poolFinishedStartLevel;
	}
	/***
	 * Returns the current message that will display when an error in the pool stops the next worker from starting
	 * @return the current message that will display when an error in the pool stops the next worker from starting
	 */
	public static String getPoolStopNextWorkerOnError() {
		return poolStopNextWorkerOnError;
	}
	/***
	 * Used to override the default message that will display when an error in the pool stops the next worker from starting. Takes one parameter, the name of the pool.
	 * @param workerComplete the new message that will display when an error in the pool stops the next worker from starting
	 */
	public static void setPoolStopNextWorkerOnError(String poolStopNextWorkerOnError) {
		LoggingTemplate.poolStopNextWorkerOnError = poolStopNextWorkerOnError;
	}
	/***
	 * Returns the logging level for when an error in the pool stops the next worker from starting
	 * @return the logging level for when an error in the pool stops the next worker from starting
	 */
	public static LoggingLevel getPoolStopNextWorkerOnErrorLevel() {
		return poolStopNextWorkerOnErrorLevel;
	}
	/***
	 * Used to overwrite the default logging level when an error in the pool stops the next worker from starting
	 * @param workerCompleteLevel the new logging level when an error in the pool stops the next worker from starting
	 */
	public static void setPoolStopNextWorkerOnErrorLevel(LoggingLevel poolStopNextWorkerOnErrorLevel) {
		LoggingTemplate.poolStopNextWorkerOnErrorLevel = poolStopNextWorkerOnErrorLevel;
	}
	/***
	 * Returns the current message that will display when pool exit the with code -1 because of an error in the pool.
	 * @return the current message that will display when pool exit the with code -1 because of an error in the pool.
	 */
	public static String getPoolExitOnError() {
		return poolExitOnError;
	}
	/***
	 * Used to override the default message that will display when pool exit the with code -1 because of an error in the pool. Takes one parameter, the name of the pool.
	 * @param workerComplete the new message that will display when pool exit the with code -1 because of an error in the pool.
	 */
	public static void setPoolExitOnError(String poolExitOnError) {
		LoggingTemplate.poolExitOnError = poolExitOnError;
	}
	/***
	 * Returns the logging level for when pool exit the with code -1 because of an error in the pool.
	 * @return the logging level for when pool exit the with code -1 because of an error in the pool.
	 */
	public static LoggingLevel getPoolExitOnErrorLevel() {
		return poolExitOnErrorLevel;
	}
	/***
	 * Used to overwrite the default logging level when pool exit the with code -1 because of an error in the pool.
	 * @param workerCompleteLevel the new logging level when pool exit the with code -1 because of an error in the pool.
	 */
	public static void setPoolExitOnErrorLevel(LoggingLevel poolExitOnErrorLevel) {
		LoggingTemplate.poolExitOnErrorLevel = poolExitOnErrorLevel;
	}
}
