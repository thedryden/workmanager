package org.thedryden.workmanager;

import java.util.function.Function;

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
	protected static Level workerStartLevel = Level.info;
	protected static String workerComplete = "Worker {} Completed with status: {}. It ran for {}";
	protected static Level workerCompleteLevel = Level.info;
	protected static String workerError = "An error occured while running the thread: {}";
	protected static Level workerErrorLevel = Level.error;
	protected static String poolStartAllPools = "Starting all pools. There are {} pools.";
	protected static Level poolStartAllPollsLevel = Level.info;
	protected static String poolEmpty = "Pool {} is empty, nothing to start";
	protected static Level poolEmptyLevel = Level.debug;
	protected static String poolStopingNextPoolOnError = "Stopping executiong because pool: {}  did not return success.";
	protected static Level poolStopingNextPoolOnErrorLevel = Level.warn;
	protected static Level poolStartLevel = Level.info;
	protected static String poolStartFinished = "Finished running all pools. It took {}.";
	protected static Level poolFinishedStartLevel = Level.info;
	protected static String poolStopNextWorkerOnError = "Since stop all running on failure is set to true, and at least one thread in the pool has failed, the pool {} will not start any more threads.";
	protected static Level poolStopNextWorkerOnErrorLevel = Level.info;
	protected static String poolExitOnError = "Pool {} failed so this application is exiting with status code -1.";
	protected static Level poolExitOnErrorLevel = Level.error;
	protected static String retry = "{} {} failed, but its has been set to retry {} times. Starting retry {} after waiting {} seconds.";
	protected static Level retryLevel = Level.warn;
	private static Function<Timer,String> timerToString = t -> {
		return t.toFancyString();
	};
	/***
	 * Takes an existing logger and allows you to select a LoggingLevel at run time, rather than having to hard code your selection.
	 * @param logger the logger you wish to call
	 * @param level The levle you wish to send the message at
	 * @param msg the message string to be logged
	 */
	public static void log( Logger logger, Level level, String msg) {
		if(logger == null) {
			//do nothing
		} else if(Level.debug.equals(level))
			logger.debug(msg);
		else if (Level.error.equals(level))
			logger.error(msg);
		else if (Level.info.equals(level))
			logger.info(msg);
		else if (Level.trace.equals(level))
			logger.trace(msg);
		else if(Level.warn.equals(level))
			logger.warn(msg);
	}
	/***
	 * Takes an existing logger and allows you to select a LoggingLevel at run time, rather than having to hard code your selection.
	 * @param logger the logger you wish to call
	 * @param level The level you wish to send the message at
     * @param format the format string
     * @param arg    the argument
	 */
	public static void log( Logger logger, Level level, String format, Object arg) {
		if(logger == null) {
			//do nothing
		} else if(Level.debug.equals(level))
			logger.debug(format, arg);
		else if (Level.error.equals(level))
			logger.error(format, arg);
		else if (Level.info.equals(level))
			logger.info(format, arg);
		else if (Level.trace.equals(level))
			logger.trace(format, arg);
		else if(Level.warn.equals(level))
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
	public static void log( Logger logger, Level level, String format,  Object arg1, Object arg2) {
		if(logger == null) {
			//do nothing
		} else if(Level.debug.equals(level))
			logger.debug(format, arg1, arg2);
		else if (Level.error.equals(level))
			logger.error(format, arg1, arg2);
		else if (Level.info.equals(level))
			logger.info(format, arg1, arg2);
		else if (Level.trace.equals(level))
			logger.trace(format, arg1, arg2);
		else if(Level.warn.equals(level))
			logger.warn(format, arg1, arg2);
	}
	/***
	 * Takes an existing logger and allows you to select a LoggingLevel at run time, rather than having to hard code your selection.
	 * @param logger the logger you wish to call
	 * @param level The level you wish to send the message at
     * @param format    the format string
     * @param arguments a list of 3 or more argument
	 */
	public static void log( Logger logger, Level level, String format, Object... arguments ) {
		if(logger == null) {
			//do nothing
		} else if(Level.debug.equals(level))
			logger.debug(format, arguments);
		else if (Level.error.equals(level))
			logger.error(format, arguments);
		else if (Level.info.equals(level))
			logger.info(format, arguments);
		else if (Level.trace.equals(level))
			logger.trace(format, arguments);
		else if(Level.warn.equals(level))
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
	public static Level getWorkerStartLevel() {
		return workerStartLevel;
	}
	/***
	 * Used to overwrite the logging level for when a worker is started.
	 * @param workerStartLevel the new logging level for when a worker is started.
	 */
	public static void setWorkerStartLevel(Level workerStartLevel) {
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
	public static Level getWorkerCompleteLevel() {
		return workerCompleteLevel;
	}
	/***
	 * Used to overwrite the logging level for when a worker completes its work.
	 * @param workerCompleteLevel the new logging level for when a worker completes its work.
	 */
	public static void setWorkerCompleteLevel(Level workerCompleteLevel) {
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
	 * @param workerError the new default message that will display when a worker stops with an error
	 */
	public static void setWorkerError(String workerError) {
		LoggingTemplate.workerError = workerError;
	}
	/***
	 * Returns the logging level for when a worker is stopped by an error.
	 * @return the logging level for when a worker is stopped by an error.
	 */
	public static Level getWorkerErrorLevel() {
		return workerErrorLevel;
	}
	/***
	 * Used to overwrite the logging level for when a worker is stopped by an error.
	 * @param workerErrorLevel the new logging level for when a worker is stopped by an error.
	 */
	public static void setWorkerErrorLevel(Level workerErrorLevel) {
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
	 * @param poolStartAllPools the new message that will display when WorkerPool starts all pools
	 */
	public static void setPoolStartAllPools(String poolStartAllPools) {
		LoggingTemplate.poolStartAllPools = poolStartAllPools;
	}
	/***
	 * Returns the logging level for when WorkerPool starts all pools.
	 * @return the logging level for when WorkerPool starts all pools.
	 */
	public static Level getPoolStartAllPollsLevel() {
		return poolStartAllPollsLevel;
	}
	/***
	 * Used to overwrite the logging level for when WorkerPool starts all pools.
	 * @param poolStartAllPollsLevel the new logging level for when WorkerPool starts all pools.
	 */
	public static void setPoolStartAllPollsLevel(Level poolStartAllPollsLevel) {
		LoggingTemplate.poolStartAllPollsLevel = poolStartAllPollsLevel;
	}
	/***
	 * Returns the current message that will display when WorkerPool starts but there is noting in the pool.
	 * @return the current message that will display when WorkerPool starts but there is noting in the pool.
	 */
	public static String getPoolEmpty() {
		return poolEmpty;
	}
	/***
	 * Used to override the default message that will display when WorkerPool starts but there is noting in the pool. Expects one argument, the name of the pool.
	 * @param poolEmpty the new message that will display when WorkerPool starts all pools
	 */
	public static void setPoolEmpty(String poolEmpty) {
		LoggingTemplate.poolEmpty = poolEmpty;
	}
	/***
	 * Returns the logging level for when WorkerPool starts but there is noting in the pool
	 * @return the logging level for when WorkerPool starts but there is noting in the pool
	 */
	public static Level getPoolEmptyLevel() {
		return poolEmptyLevel;
	}
	/***
	 * Used to overwrite the logging level for when WorkerPool starts but there is noting in the pool.
	 * @param poolEmptyLevel the new logging level for when WorkerPool starts but there is noting in the pool.
	 */
	public static void setPoolEmptyLevel(Level poolEmptyLevel) {
		LoggingTemplate.poolEmptyLevel = poolEmptyLevel;
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
	 * @param poolStopingAllOnError the new message that will display when WorkerPool stops the next pool from starting because the previous pool errored.
	 */
	public static void setPoolStopingNextPoolOnError(String poolStopingAllOnError) {
		LoggingTemplate.poolStopingNextPoolOnError = poolStopingAllOnError;
	}
	/***
	 * Returns the logging level for when WorkerPool stops the next pool from starting because the previous pool errored.
	 * @return the logging level for when WorkerPool stops the next pool from starting because the previous pool errored.
	 */
	public static Level getPoolStopingNextPoolOnErrorLevel() {
		return poolStopingNextPoolOnErrorLevel;
	}
	/***
	 * Used to overwrite the default logging level when WorkerPool stops new threads from starting because the previous pool errored.
	 * @param poolStopingAllOnErrorLevel the new logging level when WorkerPool stops new threads from starting because the previous pool errored.
	 */
	public static void setPoolStopingNextPoolOnErrorLevel(Level poolStopingAllOnErrorLevel) {
		LoggingTemplate.poolStopingNextPoolOnErrorLevel = poolStopingAllOnErrorLevel;
	}
	/***
	 * Returns the logging level when starting a pool
	 * @return the logging level when starting a pool.
	 */
	public static Level getPoolStartLevel() {
		return poolStartLevel;
	}
	/***
	 * Used to overwrite the default logging level for starting a pool. Note: the message itself can't be overwritten.
	 * @param poolStartLevel the new logging level for starting a pool.
	 */
	public static void setPoolStartLevel(Level poolStartLevel) {
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
	 * @param poolStartFinished the new message that will display when all pools complete after calling start.
	 */
	public static void setPoolStartFinished(String poolStartFinished) {
		LoggingTemplate.poolStartFinished = poolStartFinished;
	}
	/***
	 * Returns the logging level for when all pools complete after calling start.
	 * @return the logging level for when all pools complete after calling start.
	 */
	public static Level getPoolFinishedStartLevel() {
		return poolFinishedStartLevel;
	}
	/***
	 * Used to overwrite the default logging level when all pools complete after calling start.
	 * @param poolFinishedStartLevel the new logging level when all pools complete after calling start.
	 */
	public static void setPoolFinishedStartLevel(Level poolFinishedStartLevel) {
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
	 * @param poolStopNextWorkerOnError the new message that will display when an error in the pool stops the next worker from starting
	 */
	public static void setPoolStopNextWorkerOnError(String poolStopNextWorkerOnError) {
		LoggingTemplate.poolStopNextWorkerOnError = poolStopNextWorkerOnError;
	}
	/***
	 * Returns the logging level for when an error in the pool stops the next worker from starting
	 * @return the logging level for when an error in the pool stops the next worker from starting
	 */
	public static Level getPoolStopNextWorkerOnErrorLevel() {
		return poolStopNextWorkerOnErrorLevel;
	}
	/***
	 * Used to overwrite the default logging level when an error in the pool stops the next worker from starting
	 * @param poolStopNextWorkerOnErrorLevel the new logging level when an error in the pool stops the next worker from starting
	 */
	public static void setPoolStopNextWorkerOnErrorLevel(Level poolStopNextWorkerOnErrorLevel) {
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
	 * @param poolExitOnError the new message that will display when pool exit the with code -1 because of an error in the pool.
	 */
	public static void setPoolExitOnError(String poolExitOnError) {
		LoggingTemplate.poolExitOnError = poolExitOnError;
	}
	/***
	 * Returns the logging level for when pool exit the with code -1 because of an error in the pool.
	 * @return the logging level for when pool exit the with code -1 because of an error in the pool.
	 */
	public static Level getPoolExitOnErrorLevel() {
		return poolExitOnErrorLevel;
	}
	/***
	 * Used to overwrite the default logging level when pool exit the with code -1 because of an error in the pool.
	 * @param poolExitOnErrorLevel the new logging level when pool exit the with code -1 because of an error in the pool.
	 */
	public static void setPoolExitOnErrorLevel(Level poolExitOnErrorLevel) {
		LoggingTemplate.poolExitOnErrorLevel = poolExitOnErrorLevel;
	}
	/***
	 * Returns the current message that will display when either a worker or pool retries after a failure.
	 * @return the current message that will display when either a worker or pool retries after a failure.
	 */
	public static String getRetry() {
		return retry;
	}
	/***
	 * Used to override the default message that will display when either a worker or pool retries after a failure. Takes 5 parameters: 1) If this is a worker or pool, 2) the threadName of the worker or name of the pool 3) the number value of retryAttempts, 4) the current retry number, 5) the value of retryWaitSeconds.
	 * @param retry the new message that will display when either a worker or pool retries after a failure.
	 */
	public static void setRetry(String retry) {
		LoggingTemplate.retry = retry;
	}
	/***
	 * Returns the logging level for when either a worker or pool retries after a failure.
	 * @return the logging level for when either a worker or pool retries after a failure.
	 */
	public static Level getRetryLevel() {
		return retryLevel;
	}
	/***
	 * Used to overwrite the default logging level when either a worker or pool retries after a failure. This logging level will also be used to log the error that caused the retry.
	 * @param retryLevel the new logging level when either a worker or pool retries after a failure.
	 */
	public static void setRetryLevel(Level retryLevel) {
		LoggingTemplate.retryLevel = retryLevel;
	}
	/***
	 * Returns the function used to convert a Timer to a string for logging.
	 * @return the function used to convert a Timer to a string for logging.
	 */
	public static Function<Timer, String> getTimerToString() {
		return timerToString;
	}
	/***
	 * Sets the function used to convert a Timer to a string for logging. Default is .toString()
	 * @param timerToString the function used to convert a Timer to a string for logging.
	 */
	public static void setTimerToString(Function<Timer, String> timerToString) {
		LoggingTemplate.timerToString = timerToString;
	}
	/***
	 * Helper function for directly applying the TimerToString function to a timer in one call.
	 * @param aTimer a timer you wish to convert to a string
	 * @return a string that represents the timer
	 */
	public static String applyTimerToString(Timer aTimer) {
		return timerToString.apply(aTimer);
	}
}
