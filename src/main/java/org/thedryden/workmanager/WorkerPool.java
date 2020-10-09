package org.thedryden.workmanager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/***
 * WorkerPool is designed for easily setting up a multi-threaded, but static, operations that include a number of features to make managing the entire process easier.
 * When working with WorkerPool there are 2 key concepts to keep in mind: pools and workers. 
 * A pool is a collection of workers. When a pool is started up to the max thread count of workers in the pool will be started. 
 * If there are some workers in the pool that must wait for the completion of one or more other workers in the pool before they can safely be started you can set a precedence constraint on that worker. 
 * Precedence Constraints are simple a list of thread names (Strings) of the other "parent" workers that the "child" worker needs to wait for.
 * If a parent worker fails, the child worker will not be started.
 * Finally you can have more than one pool, allowing you to create larger logical blocks of code that will run in sequence.
 * 
 * Workers are objects that once started will do the actual work. 
 * To create a worker simply create your own class that extends the worker class Worker.
 * That class has all of the required methods implemented, except, the worker method, which you will need to overwrite and implement with your own code.
 * The worker method expects critical errors to be thrown, where they will be caught by the class you extended, and the status will be updated to failed automatically.
 * Therefore you should only catch errors if they do not represent the class failing and you want your the code in that class to continue executing.
 * Remember that even if one worker fails, by default, all other workers in the pool will still run, and any other pools will still execute, that behavior is controlled by stopAllRunningOnFailure and dontStartNextPoolOnFailure respectively.
 * 
 * @author Matthew Dryden
 *
 */
public class WorkerPool {
	protected Logger logger;
	
	protected List<String> keys;
	protected Map<String,List<WorkerInterface>> pools;
	protected Map<String,Integer> poolRetry;
	protected Map<String,Integer> poolRetryWaitSeconds;
	protected Map<String,Boolean> poolRetryRerunSuccess;
	protected Map<String,Thread> poolRunning;
	protected Thread dummy;
	protected List<String> notInAll;
	protected Map<String,Long> lastMsg;
	protected Map<String,Long> lastWarn;
	protected Integer threadCount;
	
	/***
	 * Default maximum number of threads running at one time
	 */
	public static final int DEFAULT_MAX_THREAD_COUNT = 3;
	protected int maxThreadCount;
	
	/***
	 * Default value for stopAllRunningOnFailure.
	 */
	public static final boolean DEFAULT_NO_NEW_THREADS_ON_FAILURE = false;
	protected boolean noNewThreadsOnFailure;
	
	/***
	 * Default value for start next pool on failure
	 */
	public static final boolean START_NEXT_POOL_ON_FAILURE = true;
	protected boolean startNextPoolOnFailure; 
	
	/***
	 * Default seconds between status messages
	 */
	public static final long DEFAULT_SECONDS_BETWEEN_MSG = 600;
	protected long secondsBetweenMsg;
	/***
	 * Default seconds before warning
	 */
	public static final long DEFAULT_SECONDS_BEFORE_WARN = 1_200;
	protected long secondsBeforeWarn;
	
	/***
	 * Default value for exit on error
	 */
	public static final boolean DEFAULT_EXIT_ON_ERROR = true;
	protected boolean exitOnError;
	
	private String lastPoolKey = null;
	private WorkerInterface lastWorker = null;
	/***
	 * Creates a new worker pool.
	 */
	public WorkerPool() {
		this(null);
	}
	/***
	 * Creates a new WorkerPool that will use the passed logger. If null is passed for the logger one will be created for you.
	 * @param logger a logger for the new WorkerPool. If null is passed for the logger one will be created for you.
	 */
	public WorkerPool(Logger logger) {
		if(logger == null)
			this.logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
		else
			this.logger = logger;
		keys = new ArrayList<>();
		pools = new Hashtable<>();
		poolRetry = new Hashtable<>();
		poolRetryWaitSeconds = new Hashtable<>(); 
		poolRetryRerunSuccess = new Hashtable<>(); 
		lastMsg = new Hashtable<>();
		lastWarn = new Hashtable<>();
		maxThreadCount = DEFAULT_MAX_THREAD_COUNT;
		noNewThreadsOnFailure = DEFAULT_NO_NEW_THREADS_ON_FAILURE;
		secondsBetweenMsg = DEFAULT_SECONDS_BETWEEN_MSG;
		secondsBeforeWarn = DEFAULT_SECONDS_BEFORE_WARN;
		startNextPoolOnFailure = START_NEXT_POOL_ON_FAILURE;
		exitOnError = DEFAULT_EXIT_ON_ERROR;
		notInAll = new ArrayList<>();
		poolRunning = new Hashtable<>();
		dummy = new Thread();
		dummy.setName("dummy");
		threadCount = 0;
	}
	/***
	 * Sets the maximum number of threads that can be run at once.
	 * @param maxThreadCount the new maximum number of threads that can be run at once.
	 * @return this - for method chaining.
	 */
	public WorkerPool setMaxThreadCount( int maxThreadCount ) {
		this.maxThreadCount = maxThreadCount;
		return this;
	}
	/***
	 * Returns the current value maxThreadCount
	 * @return the current value maxThreadCount
	 */
	public int getMaxThreadCount() {
		return maxThreadCount;
	}
	/***
	 * Sets the number of seconds between sending status messages. It is not guaranteed that status messages will be sent at exactly the provided time interval, particularly if the interval is less than 30 seconds.
	 * @param timeBetweenMsg seconds between sending status messages while running.
	 * @return this - for method chaining.
	 */
	public WorkerPool setSecondsBetweenMsg(long timeBetweenMsg) {
		this.secondsBetweenMsg = timeBetweenMsg;
		return this;
	}
	/***
	 * Returns the time between status messages.
	 * @return the time between status messages.
	 */
	public long getSecondsBetweenMsg() {
		return secondsBetweenMsg;
	}
	/***
	 * Returns the time between warning messages.
	 * @return the time between warning messages.
	 */
	public long getSecondsBeforeWarn() {
		return secondsBeforeWarn;
	}
	/***
	 * Sets the number of seconds between sending warning status messages. These messages differ from status messages only that they are sent with a Level of warning.
	 * @param secondsBeforeWarn the new time between warning messages.
	 */
	public void setSecondsBeforeWarn(long secondsBeforeWarn) {
		this.secondsBeforeWarn = secondsBeforeWarn;
	}
	/***
	 * If set to true then if any threads fail in a pool, no more workers in that pool will be started.
	 * @param noNewThreadsOnFailure the new value for stopAllRunningOnFailure
	 * @return this - for method chaining.
	 */
	public WorkerPool setNoNewThreadsOnFailure( boolean noNewThreadsOnFailure ) {
		this.noNewThreadsOnFailure = noNewThreadsOnFailure;
		return this;
	}
	/***
	 * Returns stopAllRunningOnFailure
	 * @return stopAllRunningOnFailure
	 */
	public boolean isNoNewThreadsOnFailure() {
		return noNewThreadsOnFailure;
	}
	/***
	 * Returns the value for the property: don't start next pool on failure.
	 * @return the value for the property: don't start next pool on failure.
	 */
	public boolean isStartNextPoolOnFailure() {
		return startNextPoolOnFailure;
	}
	/***
	 * Used to set the value for the property: start next pool on failure. When this is false and either start or startPools is called they will not start the next pool if the previous pool failed. If this is true, then the next pool will start regardless of the status of the last pool.
	 * @param startNextPoolOnFailure the new value for don't start next pool on failure.
	 * @return this - for method chaining.
	 */
	public WorkerPool setStartNextPoolOnFailure(boolean startNextPoolOnFailure) {
		this.startNextPoolOnFailure = startNextPoolOnFailure;
		return this;
	}
	/***
	 * If true when start, startPools, startAsyncAndJoin, or startOnePool is called at the end of execution of all pools, if the aggregate status for all pools is failure then start will cause System.exit(-1) to be called.
	 * If false do nothing
	 * @return the value for exitOnError
	 */
	public boolean isExitOnError() {
		return exitOnError;
	}
	/***
	 * Used to set exitOnError. If true when start, startPools, startAsyncAndJoin, or startOnePool is called at the end of execution of all pools, if the aggregate status for all pools is failure then start will cause System.exit(-1) to be called.
	 * @param exitOnError the value you wish to set  for exit on error
	 * @return this - for method chaining
	 */
	public WorkerPool setExitOnError(boolean exitOnError) {
		this.exitOnError = exitOnError;
		return this;
	}
	/***
	 * Allows you to pass a list of the name of all the thread pools you want to create.
	 * @param poolNames  a list of the name of all the thread pools you want to create.
	 * @return this - for method chaining.
	 */
	public WorkerPool setPools( List<String> poolNames ) {
		keys = poolNames;
		pools.clear();
		for(String aKey : keys )
			pools.put(aKey, new ArrayList<WorkerInterface>());
		lastPoolKey = null;
		return this;
	}
	/***
	 * Adds a pool of threads. When you create a pool this way you can call the set and add Workers methods that don't require passing a pool name and they will automatically be attached to the last pool you added with this method.
	 * @param poolName the name of a pool you want to create
	 * @return this - for method chaining.
	 */
	public WorkerPool addPool( String poolName ) {
		keys.add(poolName);
		pools.put(poolName, new ArrayList<WorkerInterface>());
		lastPoolKey = poolName;
		return this;
	}
	/***
	 * Returns a copy of the list of poolNames. Because this is a copy, editing this will not effect operations
	 * @return a copy of the list of poolNames
	 */
	public List<String> getPoolNames(){
		List<String> output = new ArrayList<>();
		for(String aKey : keys)
			output.add(aKey);
		return output;
	}
	/***
	 * For the last pool added sets a number of retry to attempt on a failure and the number of seconds to wait before after a failure before the retry.
	 * @param retryAttempts the number of times you wish to retry the pool in the even of a failure
	 * @param retryWaitSeconds the number of seconds to wait after a failure before you retry.
	 * @param rerunSuccess if true all workers will be rerun. If false then any workers that already completed with status success will be skipped.
	 * @return this - for method chaining
	 */
	public WorkerPool setRetry(int retryAttempts, int retryWaitSeconds, boolean rerunSuccess) {
		if(lastPoolKey == null)
			throw new ArrayIndexOutOfBoundsException("No pools have been added yet");	
		return setRetry(lastPoolKey, retryAttempts, retryWaitSeconds, rerunSuccess);
	}
	/***
	 * For the passed poolName added sets a number of retry to attempt on a failure and the number of seconds to wait before after a failure before the retry.
	 * @param poolName the name of the pool you wish to configure
	 * @param retryAttempts the number of times you wish to retry the pool in the even of a failure
	 * @param retryWaitSeconds the number of seconds to wait after a failure before you retry.
	 * @param rerunSuccess if true all workers will be rerun. If false then any workers that already completed with status success will be skipped.
	 * @return this - for method chaining
	 */
	public WorkerPool setRetry(String poolName, int retryAttempts, int retryWaitSeconds, boolean rerunSuccess) {
		poolRetry.put(poolName, retryAttempts);
		poolRetryWaitSeconds.put(poolName, retryWaitSeconds);
		poolRetryRerunSuccess.put(poolName, rerunSuccess);
		return this;
	}
	/***
	 * Returns the number of retry attempts for the passed poolName
	 * @param poolName the pool name you wish to lookup a value for
	 * @return the number of retry attempts for the passed poolName
	 */
	public int getRetryAttempt(String poolName) {
		return poolRetry.get(poolName);
	}
	/***
	 * Returns the number of seconds to wait after a failure to retry
	 * @param poolName the pool name you wish to lookup a value for
	 * @return the number of seconds to wait after a failure to retry
	 */
	public int getRetryWaitSeconds(String poolName) {
		return poolRetryWaitSeconds.get(poolName);
	}
	/***
	 * Takes a pool name and a list of worker objects and sets / replaces the entire list of workers associated within that pool
	 * @param poolName the name of the pool you want to attach the list of workers to.
	 * @param workers the list of workers you with to set / replace the workers for the pool with
	 * @return this - for method chaining.
	 */
	public WorkerPool setWorkers( String poolName, List<WorkerInterface> workers ) {
		pools.put(poolName, workers);
		return this;
	}
	/***
	 * Takes a list of worker objects and sets / replaces the entire list of workers associated with the last pool added with addPool
	 * @param workers the list of workers you with to set / replace the workers for the pool with
	 * @return this - for method chaining.
	 */
	public WorkerPool setWorkers( List<WorkerInterface> workers ) {
		if(lastPoolKey == null)
			throw new ArrayIndexOutOfBoundsException("No pools have been added yet");		
		pools.put(lastPoolKey, workers);		
		return this;
	}
	/***
	 * Takes a pool name and adds a single worker to the end of that pools list of workers.
	 * @param poolName the name of the pool you want to attach add the worker too.
	 * @param newWorker a single worker you wish to add to the pool
	 * @return this - for method chaining.
	 */
	public WorkerPool addWorker( String poolName, WorkerInterface newWorker ) {
		pools.get(poolName).add(newWorker);
		lastWorker = newWorker;
		return this;
	}
	/***
	 * Adds a worker to the end of the pool associated with the last pool added with addPool
	 * @param newWorker a single worker you wish to add to the pool
	 * @return this - for method chaining.
	 */
	public WorkerPool addWorker( WorkerInterface newWorker ) {
		if(lastPoolKey == null)
			throw new ArrayIndexOutOfBoundsException("No pools have been added yet");	
		pools.get(lastPoolKey).add(newWorker);
		lastWorker = newWorker;
		return this;
	}
	/***
	 * Sets the last worker added with either the addWorker method to the Not In All list. Workers in the Not In All pool will not be run if ALL arg is passed as the runArg, or no runArg is passed at all. To run these you must explicitly add the name of the name of the worker to the runArg.
	 * @return this - for method chaining.
	 */
	public WorkerPool setNotInAll() {
		notInAll.add(lastWorker.getThreadName());
		return this;
	}
	/***
	 * Takes a worker's thread name and adds it to the Not In All list. Workers in the Not In All pool will not be run if ALL arg is passed as the runArg, or no runArg is passed at all. To run these you must explicitly add the name of the name of the worker to the runArg.
	 * @param workerThreadName a threadName to add to the Not In All List
	 * @return this - for method chaining.
	 */
	public WorkerPool setNotInAll(String workerThreadName) {
		notInAll.add(workerThreadName);
		return this;
	}
	/***
	 * Used to overwrite the thread name of the previously added worker.
	 * @param threadName The new name for the thread
	 * @return this - for method chaining
	 */
	public WorkerPool setThreadName(String threadName) {
		if(lastWorker == null)
			throw new ArrayIndexOutOfBoundsException("No workers have been added yet");
		lastWorker.setThreadName(threadName);
		return this;
	}
	/***
	 * Used to prepend a string to the thread name of the previously added worker.
	 * @param prepend The string you wish to prepend to the thread name
	 * @return this - for method chaining
	 */
	public WorkerPool prependThreadName(String prepend) {
		if(lastWorker == null)
			throw new ArrayIndexOutOfBoundsException("No workers have been added yet");
		String threadName = prepend + lastWorker.getThreadName();
		lastWorker.setThreadName(threadName);
		return this;
	}
	/***
	 * Used to append a string to the thread name of the previously added worker.
	 * @param append The string you wish to append to the thread name
	 * @return this - for method chaining
	 */
	public WorkerPool appendThreadName(String append) {
		if(lastWorker == null)
			throw new ArrayIndexOutOfBoundsException("No workers have been added yet");
		String threadName = lastWorker.getThreadName() + append;
		lastWorker.setThreadName(threadName);
		return this;
	}
	/***
	 * Returns the thread name of the last worker added.
	 * @return the thread name of the last worker added
	 */
	public String getThreadName() {
		if(lastWorker == null)
			throw new ArrayIndexOutOfBoundsException("No workers have been added yet");
		return lastWorker.getThreadName();
	}
	/***
	 * Allows you to set the entire precedence constraint of the previously added worker. Pass null to remove any precedence constraints associated with the worker.
	 * @param precedenceConstraint the new precedence constraints you wish the last worker added has.
	 * @return this - for method chaining
	 */
	public WorkerPool setPrecedenceConstraint(Set<String> precedenceConstraint) {
		if(lastWorker == null)
			throw new ArrayIndexOutOfBoundsException("No workers have been added yet");
		lastWorker.setPrecedenceConstraint(precedenceConstraint);
		return this;
	}
	/***
	 * Adds a precedence constraint to the last worker added. 
	 * @param precedenceConstraint the name of a thread you wish to complete before the last worker starts.
	 * @return this - for method chaining
	 */
	public WorkerPool addPrecedenceConstraint(String precedenceConstraint) {
		if(lastWorker == null)
			throw new ArrayIndexOutOfBoundsException("No workers have been added yet");
		Set<String> currentPrecedenceConstraint = null;
		if(lastWorker.getPrecedenceConstraint() == null)
			currentPrecedenceConstraint = new HashSet<>();
		else
			currentPrecedenceConstraint = lastWorker.getPrecedenceConstraint();
		currentPrecedenceConstraint.add(precedenceConstraint);
		lastWorker.setPrecedenceConstraint(currentPrecedenceConstraint);
		return this;
	}
	/***
	 * Allows you to set the entire precedence constraint of the worker with the passed thread name. Pass null to remove any precedence constraints associated with the worker.
	 * @param threadName The name of the worker you are looking to edit.
	 * @param precedenceConstraint the new precedence constraints you wish the last worker added has.
	 * @return this - for method chaining
	 */
	public WorkerPool setPrecedenceConstraint(String threadName, Set<String> precedenceConstraint) {
		WorkerInterface target = getWorker(threadName);
		target.setPrecedenceConstraint(precedenceConstraint);
		return this;
	}
	/***
	 * Adds a precedence constraint to the worker with the passed thread name. 
	 * @param threadName The name of the worker you are looking to edit.
	 * @param precedenceConstraint the new precedence constraints you wish the last worker added has.
	 * @return this - for method chaining
	 */
	public WorkerPool addPrecedenceConstraint(String threadName, String precedenceConstraint) {
		WorkerInterface target = getWorker(threadName);
		Set<String> currentPrecedenceConstraint = target.getPrecedenceConstraint();
		currentPrecedenceConstraint.add(precedenceConstraint);
		target.setPrecedenceConstraint(currentPrecedenceConstraint);
		return this;
	}
	/***
	 * For the last worker added sets a number of retry to attempt on a failure and the number of seconds to wait before after a failure before the retry.
	 * @param retryAttempts the number of times you wish to retry the pool in the even of a failure
	 * @param retryWaitSeconds the number of seconds to wait after a failure before you retry.
	 * @return this - for method chaining
	 */
	public WorkerPool setWorkerRetry(int retryAttempts, int retryWaitSeconds) {
		if(lastWorker == null)
			throw new ArrayIndexOutOfBoundsException("No workers have been added yet");
		lastWorker.setRetry(retryAttempts, retryWaitSeconds);
		return this;
	}
	/***
	 * For the passed worker added sets a number of retry to attempt on a failure and the number of seconds to wait before after a failure before the retry.
	 * @param threadName the name of the worker you wish to modify.
	 * @param retryAttempts the number of times you wish to retry the pool in the even of a failure
	 * @param retryWaitSeconds the number of seconds to wait after a failure before you retry.
	 * @return this - for method chaining
	 */
	public WorkerPool setWorkerRetry(String threadName, int retryAttempts, int retryWaitSeconds) {
		getWorker(threadName).setRetry(retryAttempts, retryWaitSeconds);
		return this;
	}

	private int threadMinus() {
		int out = 0;
		synchronized(threadCount) {
			threadCount--;
			out = threadCount;
		}
		return out;
	}
	
	private int getThreadCount() {
		int out = 0;
		synchronized(threadCount) {
			out = threadCount;
		}
		return out;
	}
	
	/***
	 * Takes a name of a worker that is in any pool, and returns that worker.
	 * @param threadName the name of the Worker you are searching for
	 * @return the worker, as a WorkerInterface
	 * @throws ArrayIndexOutOfBoundsException thrown if the threadName is not in any pool
	 */
	public WorkerInterface getWorker(String threadName) throws ArrayIndexOutOfBoundsException {
		for(String aKey : keys) {
			for(WorkerInterface aWorker : pools.get(aKey)) {
				if(aWorker.getThreadName().equals(threadName)) {
					return aWorker;
				}
			}
		}
		throw new ArrayIndexOutOfBoundsException("Could not find worker with name: " + threadName);
	}
	
	private void checkThreadNames() throws DuplicateThreadNameException {
		Set<String> dups = new HashSet<>();
		Set<String> allThreadNames = new HashSet<>();
		for(String aKey : keys) {
			for(WorkerInterface aWorker : pools.get(aKey)) {
				if(allThreadNames.contains(aWorker.getThreadName())) 
					dups.add(aWorker.getThreadName());
				allThreadNames.add(aWorker.getThreadName());
			}
		}
		
		if(dups.size() > 0) {
			StringBuilder output = new StringBuilder("Thread names must be globally unique both within and accross pools. The following thread name(s) appears more than once: ");
			First first = new First();
			for(String aName : dups) {
				if(!first.first())
					output.append(", ");
				output.append(aName);
			}
			throw new DuplicateThreadNameException(output.toString());
		}
	}
	
	private void checkForPrecedenceLoop() throws CircularPrecedenceConstraintException {
		for(String aKey : keys) {
			for(WorkerInterface aWorker : pools.get(aKey)) {
				Set<String> pc = getCurrentPrecedenceConstraint(aWorker, aKey);
				for(String aChildName : pc) {
					WorkerInterface child = this.getWorker(aChildName);
					if(child.getPrecedenceConstraint() != null) {
						for(String aGrandChildName : child.getPrecedenceConstraint()) {
							if(aWorker.getThreadName().equalsIgnoreCase(aGrandChildName)) {
								throw new CircularPrecedenceConstraintException("The workers: " + aWorker.getThreadName() + " and " + child.getThreadName() + " both list eachother as a precedence constraint which creates and unresolveable loop.");
							}
						}
					}
				}
			}
		}
	}
	
	private boolean checkPrecedence( WorkerInterface toCheck, List<WorkerInterface> pool ) {
		if(toCheck.getPrecedenceConstraint() == null || toCheck.getPrecedenceConstraint().size() == 0)
			return true;
		for(String aConstraint : toCheck.getPrecedenceConstraint()) {
			for(WorkerInterface aWorker : pool) {
				if(aConstraint.equals(aWorker.getThreadName())) {
					if(StatusMeta.isFailed(getWorkerStatus(aWorker))) {
						toCheck.setStatus(Status.PRECEDENCE_FAILED);
						return false;
					} else if (!getWorkerStatus(aWorker).equals(Status.SUCCESS)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	/***
	 * Used to control which pools / workers will run.
	 * The concept behind this is that when this package is called the user will be able to provide a argument to control what will and will not run in this package.
	 * If all is passed everything in the package, not market as "notInAll" will run.
	 * If a pool name is passed everything that will act like you passed the name of all workers in that pool.
	 * If you put a minus (-) in front of a worker / pool name than that worker / pool will be removed from execution.
	 * @param runArg a list of pool / worker names.
	 * @return this - for method chaining.
	 */
	public WorkerPool setRunArg( List<String> runArg ) {
		return setRunArg( runArg.toArray(new String[0]) );
	}
	/***
	 * Used to control which pools / workers will run.
	 * The concept behind this is that when this package is called the user will be able to provide a argument to control what will and will not run in this package.
	 * If all is passed everything in the package, not market as "notInAll" will run.
	 * If a pool name is passed everything that will act like you passed the name of all workers in that pool.
	 * If you put a minus (-) in front of a worker / pool name than that worker / pool will be removed from execution.
	 * @param runArg a list of pool / worker names.
	 * @return this - for method chaining.
	 */
	public WorkerPool setRunArg( String[] runArg ) {
		boolean hasAll = false;
		Set<String> include = new HashSet<>();
		Set<String> exclude = new HashSet<>();
		for(int i = 0; i < runArg.length; i++) {
			//If all (which means include all that aren't marked as not in all.
			if(runArg[i].equalsIgnoreCase("ALL")) {
				hasAll = true;
			} else {
				//Set add (true by default) by checking if this is a remove (start with -)
				boolean add = true;
				if(runArg[i].startsWith("-")){
					add = false;
					runArg[i] = runArg[i].substring(1);
				//remove plus, they don't do anything but since we look for -, it possible people will get confused
				} else if(runArg[i].startsWith("+")) {
					runArg[i] = runArg[i].substring(1); 
				}
				//check if what is passed is a pool instead of a specific worker
				boolean isPool = false;
				for(String aKey : keys) {
					//if this is a pool, add all workers in the pool to either include or exclude based on add
					if(runArg[i].equalsIgnoreCase(aKey)) {
						isPool = true;
						for(WorkerInterface aWorker : pools.get(aKey)) {
							if(add) {
								include.add(aWorker.getThreadName().toUpperCase());
							} else {
								exclude.add(aWorker.getThreadName().toUpperCase());
							}
						}
						break;
					}
				}
				
				//If this is not a pool
				if(!isPool) {
					if (!add) {
						exclude.add(runArg[i].toUpperCase());
					} else {
						include.add(runArg[i].toUpperCase());
					}
				}
			}
		}
		
		if(hasAll) {
			for(String aNot : notInAll) {
				aNot = aNot.toUpperCase();
				if(!include.contains(aNot))
					exclude.add(aNot);
			}
		}
		
		for(String aKey : keys) {
			Iterator<WorkerInterface> workerItr = pools.get(aKey).iterator();
			while(workerItr.hasNext()) {
				String aName = workerItr.next().getThreadName().toUpperCase();
				if(exclude.contains(aName)) {
					workerItr.remove();
				} else if (!hasAll && !include.contains(aName)) {
					workerItr.remove();
				}
			}
		}
		return this;
	}
	/***
	 * Starts all pools in the order they were added.
	 * @return this - for method chaining.
	 * @throws InterruptedException Thrown when a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity.
	 * @throws DuplicateThreadNameException thrown if any thread name is duplicated, even across pools
	 * @throws CircularPrecedenceConstraintException thrown if two workers both list each other as Precedence Constraints
	 * @throws AlreadyRunningException thrown if a pool that is already running is asked to start
	 */
	public WorkerPool start() throws InterruptedException, DuplicateThreadNameException, CircularPrecedenceConstraintException, AlreadyRunningException {
		startPools(keys.toArray(new String[keys.size()]));
		return this;
	}
	/***
	 * Starts all pools at the same time asynchronously. Does not support startNextPoolOnFailure = false, since all pools are started at the same time.
	 * Note this does not block execution until it completes for that use startAsyncAndJoin
	 * @return this - for method chaining.
	 * @throws DuplicateThreadNameException thrown if any thread name is duplicated, even across pools
	 * @throws CircularPrecedenceConstraintException thrown if two workers both list each other as Precedence Constraints
	 * @throws AlreadyRunningException thrown if a pool that is already running is asked to start
	 */
	public WorkerPool startAsync() throws AlreadyRunningException, DuplicateThreadNameException, CircularPrecedenceConstraintException {
		if(keys.size() > 1)
			LoggingTemplate.log(logger, LoggingTemplate.getPoolStartAllPollsLevel(), LoggingTemplate.getPoolStartAllPools(), keys.size());
		//startOnePool removes the key from the pool when its done
		for(String key : keys) {
			startOnePoolAsync(key);
		}
		return this;
	}
	/***
	 * Starts all pools at the same time asynchronously and then blocks execution until they complete. Does not support startNextPoolOnFailure = false, since all pools are started at the same time.
	 * @return this - for method chaining
	 * @throws AlreadyRunningException thrown if a pool that is already running is asked to start
	 * @throws DuplicateThreadNameException thrown if any thread name is duplicated, even across pools
	 * @throws CircularPrecedenceConstraintException thrown if two workers both list each other as Precedence Constraints
	 * @throws InterruptedException Thrown when a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity.
	 */
	public WorkerPool startAsyncAndJoin() throws AlreadyRunningException, DuplicateThreadNameException, CircularPrecedenceConstraintException, InterruptedException {
		return startPoolsAsyncAndJoin(keys.toArray(new String[keys.size()]));
	}
	/***
	 * Starts the passed pool. Once completed this pool will be removed from the list of pools that will be started with the start method.
	 * @param poolName The name of the pool of workers you wish to run
	 * @return this - for method chaining.
	 * @throws InterruptedException Thrown when a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity.
	 * @throws DuplicateThreadNameException thrown if any thread name is duplicated, even across pools
	 * @throws CircularPrecedenceConstraintException thrown if two workers both list each other as Precedence Constraints
	 * @throws AlreadyRunningException thrown if a pool that is already running is asked to start
	 */
	public WorkerPool startOnePool( String poolName ) throws InterruptedException, DuplicateThreadNameException, CircularPrecedenceConstraintException, AlreadyRunningException {
		if(poolRunning.containsKey(poolName)) {
			throw new AlreadyRunningException("The pool " + poolName + " is already running.");
		} else {
			poolRunning.put(poolName,dummy);
		}
		
		checkThreadNames();
		checkForPrecedenceLoop();
		
		startOnePoolHelper(poolName);
		
		if(exitOnError && getStatus(poolName).equals(Status.FAILED)) {
			LoggingTemplate.log(logger, LoggingTemplate.getPoolExitOnErrorLevel(), LoggingTemplate.getPoolExitOnError(), poolName);
			System.exit(-1);
		}
		
		return this;
	}
	/***
	 * Starts the passed pools, running them one at a time in the order they were passed - not added. Once completed these pools will be removed from the list of pools that will be started with the start method.
	 * @param poolNames one or more poolName in a list or a array of poolNames
	 * @return this - for method chaining
	 * @throws InterruptedException Thrown when a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity.
	 * @throws DuplicateThreadNameException thrown if any thread name is duplicated, even across pools
	 * @throws CircularPrecedenceConstraintException thrown if two workers both list each other as Precedence Constraints
	 * @throws AlreadyRunningException thrown if a pool that is already running is asked to start
	 */
	public WorkerPool startPools(String ...poolNames) throws DuplicateThreadNameException, CircularPrecedenceConstraintException, InterruptedException, AlreadyRunningException {
		checkThreadNames();
		checkForPrecedenceLoop();
		
		Timer timer = new Timer().start();
		boolean log = keys.size() > 1;
		if(log)
			LoggingTemplate.log(logger, LoggingTemplate.getPoolStartAllPollsLevel(), LoggingTemplate.getPoolStartAllPools(), poolNames.length);
		
		for(int i = 0; i < poolNames.length; i++) {
			String aPoolName = poolNames[i];
			if(poolRunning.containsKey(aPoolName)) {
				throw new AlreadyRunningException("The pool " + aPoolName + " is already running.");
			} else {
				poolRunning.put(aPoolName,dummy);
			}
			
			startOnePoolHelper(aPoolName);
			
			if(!startNextPoolOnFailure && ( i + 1 ) < poolNames.length && !getStatus(aPoolName).equals(Status.SUCCESS)) {
				LoggingTemplate.log(logger, LoggingTemplate.getPoolStopingNextPoolOnErrorLevel(), LoggingTemplate.getPoolStopingNextPoolOnError(), aPoolName);
				break;
			}
		}
		
		timer.stop();
		if(log)
			LoggingTemplate.log(logger, LoggingTemplate.getPoolFinishedStartLevel(), LoggingTemplate.getPoolStartFinished(), LoggingTemplate.applyTimerToString( timer ));
		
		if(exitOnError && getStatus(poolNames).equals(Status.FAILED)) {
			LoggingTemplate.log(logger, LoggingTemplate.getPoolExitOnErrorLevel(), LoggingTemplate.getPoolExitOnError(), getFailedPools(poolNames));
			System.exit(-1);
		}
		
		return this;
	}
	/***
	 * Starts the passed pool, without blocking until completion. Once completed this pool will be removed from the list of pools that will be started with the start method.
	 * @param poolName The name of the pool of workers you wish to run
	 * @return this - for method chaining.
	 * @throws DuplicateThreadNameException thrown if any thread name is duplicated, even across pools
	 * @throws CircularPrecedenceConstraintException thrown if two workers both list each other as Precedence Constraints
	 * @throws AlreadyRunningException thrown if a pool that is already running is asked to start
	 */
	public WorkerPool startOnePoolAsync( String poolName ) throws AlreadyRunningException, DuplicateThreadNameException, CircularPrecedenceConstraintException {
		if(poolRunning.containsKey(poolName)) {
			throw new AlreadyRunningException("The pool " + poolName + " is already running.");
		} else {
			poolRunning.put(poolName,dummy);
		}
		keys.remove(poolName);
		
		checkThreadNames();
		checkForPrecedenceLoop();
		
		Thread aThread = new Thread(new PoolWrapper( this, poolName ));
		aThread.setName(poolName);
		aThread.start();
		poolRunning.put(poolName,aThread);
		
		return this;
	}
	/***
	 * Starts the passed pools, without blocking until completion. Once started a pool will be removed from the list of pools that will be started with the start method.
	 * @param poolNames one or more poolName in a list or a array of poolNames
	 * @return this - for method chaining
	 * @throws AlreadyRunningException thrown if a pool that is already running is asked to start
	 * @throws DuplicateThreadNameException thrown if any thread name is duplicated, even across pools
	 * @throws CircularPrecedenceConstraintException thrown if two workers both list each other as Precedence Constraints
	 */
	public WorkerPool startPoolsAsync(String ...poolNames) throws AlreadyRunningException, DuplicateThreadNameException, CircularPrecedenceConstraintException {
		if(poolNames.length > 1)
			LoggingTemplate.log(logger, LoggingTemplate.getPoolStartAllPollsLevel(), LoggingTemplate.getPoolStartAllPools(), poolNames.length);
		
		if(poolNames != null && poolNames.length > 0) {
			for(String aPool : poolNames) {
				startOnePoolAsync( aPool );
			}
		}
		return this;
	}
	/***
	 * Used to start multiple pool asynchronous, but then wait for all pools to complete before continuing.
	 * This is just a convince function that calls startPoolAysnc followed by joinPools (with 0 from waitMilliseconds).
	 * @param poolNames one or more poolName in a list or a array of poolNames
	 * @return this - for method chaining
	 * @throws AlreadyRunningException thrown if a pool that is already running is asked to start
	 * @throws DuplicateThreadNameException thrown if any thread name is duplicated, even across pools
	 * @throws CircularPrecedenceConstraintException thrown if two workers both list each other as Precedence Constraints
	 * @throws InterruptedException Thrown when a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity.
	 */
	public WorkerPool startPoolsAsyncAndJoin(String ...poolNames) throws AlreadyRunningException, DuplicateThreadNameException, CircularPrecedenceConstraintException, InterruptedException {
		Timer timer = new Timer().start();
		boolean log = keys.size() > 1;
		
		startPoolsAsync(poolNames);
		joinPools(0, poolNames);
		
		timer.stop();
		if(log)
			LoggingTemplate.log(logger, LoggingTemplate.getPoolFinishedStartLevel(), LoggingTemplate.getPoolStartFinished(), LoggingTemplate.applyTimerToString( timer ));
		
		if(exitOnError && getStatus(poolNames).equals(Status.FAILED)) {
			LoggingTemplate.log(logger, LoggingTemplate.getPoolExitOnErrorLevel(), LoggingTemplate.getPoolExitOnError(), getFailedPools(poolNames));
			System.exit(-1);
		}
		return this;
	}
	
	//Manages retry pool
	WorkerPool startOnePoolHelper( String poolName) throws InterruptedException, DuplicateThreadNameException, CircularPrecedenceConstraintException, AlreadyRunningException {
		if(pools.get(poolName).isEmpty()) {
			LoggingTemplate.log(logger, LoggingTemplate.getPoolEmptyLevel(), LoggingTemplate.getPoolEmpty(), poolName);
			keys.remove(poolName);
			return this;
		}
		
		int maxRetry = 0;
		if(poolRetry.containsKey(poolName))
			maxRetry = poolRetry.get(poolName);
		int retryWait = 0;
		if(poolRetryWaitSeconds.containsKey(poolName))
			retryWait = poolRetryWaitSeconds.get(poolName);
		boolean rerunSucess = false;
		if(poolRetryRerunSuccess.containsKey(poolName))
			rerunSucess = poolRetryRerunSuccess.get(poolName);
		
		int retry = 0;
		while(retry <= maxRetry) {
			if(retry > 0) {
				LoggingTemplate.log(logger, LoggingTemplate.getRetryLevel(), LoggingTemplate.getRetry(), "Pool", poolName, maxRetry, retry, retryWait);
				Thread.sleep(retryWait * 1000);
				for( WorkerInterface aWorker : pools.get(poolName) ) {
					if(rerunSucess || aWorker.getStatus() != Status.SUCCESS)
						aWorker.setStatus(Status.PENDING);
				}
			}
			startOnePoolHelperHelper(poolName);
			if(getStatus(poolName) != Status.SUCCESS) {
				retry++;
			} else {
				break;
			}
		}
		
		if(poolRunning.containsKey(poolName))
			poolRunning.remove(poolName);
		
		return this;
	}
	
	//Does the actual work to run a pool
	WorkerPool startOnePoolHelperHelper( String poolName) throws InterruptedException, DuplicateThreadNameException, CircularPrecedenceConstraintException, AlreadyRunningException {
		//Create signal to allow workers to notify this method when they're done
		Signal signal = new Signal();
		Timer timer = new Timer().start();
		List<Thread> threadPool = new ArrayList<>();
		lastMsg.put(poolName, 0L);
		lastWarn.put(poolName, 0L);
		logStart(poolName);
		//List to hold workers with precedence constraints that are waiting for the condition to be met
		List<WorkerInterface> waiting = null;
		//Loop over all workers and assign them threads or put them in waiting
		boolean keepGoing = true;
		List<WorkerInterface> myPool = new ArrayList<>();
		for( WorkerInterface aWorker : pools.get(poolName) ) 
			myPool.add(aWorker);
		
		/* Because of race conditions its possible for us to get the the synchronized block, 
		 * but the thread count to be met we can't assume we'll always deal with a worker on the first
		 * try, thus the odd loop and use of myPool
		 */
		while( !myPool.isEmpty() ) {
			WorkerInterface aWorker = myPool.get(0);
			//Check to see if worker is ready to run (based on precedence)
			if(aWorker.getPrecedenceConstraint() == null || checkPrecedence(aWorker, pools.get(poolName))) {
				waitForThreadPool(signal,threadPool,poolName,timer);
				synchronized(threadCount) {
					if(threadCount <= maxThreadCount) {
						//Create new thread, name thread, start thread, and then stored it in threadPool
						threadCount++;
						Thread aThread = new Thread(new WorkerWrapper( signal, aWorker ));
						aThread.setName(aWorker.getThreadName());
						aThread.start();
						threadPool.add(aThread);
						myPool.remove(0);
					}
				}
			} else {
			//If not yet ready, add to waiting
				if(waiting == null)
					waiting = new ArrayList<>();
				waiting.add(aWorker);
				myPool.remove(0);
			}
			
			if(noNewThreadsOnFailure && getStatus( poolName ) == Status.FAILED){
				keepGoing = false;
				LoggingTemplate.log(logger, LoggingTemplate.getPoolStopNextWorkerOnErrorLevel(), LoggingTemplate.getPoolStopNextWorkerOnError(), poolName);
				break;
			}
		}
		
		//While there is stuff in waiting to do
		while(keepGoing && waiting != null && waiting.size() > 0) {
			//Loop over all waiting workers
			Iterator<WorkerInterface> workerItr = waiting.iterator();
			while(workerItr.hasNext()) {
				WorkerInterface aWorker = workerItr.next();
				//worker is ready, start it
				if(checkPrecedence(aWorker, pools.get(poolName))){
					//Wait here if we've reached the max thread pool size
					waitForThreadPool(signal,threadPool,poolName,timer);
					//Create new thread, name thread, start thread, and then stored it in threadPool
					synchronized(threadCount) {
						if(threadCount < maxThreadCount) {
							threadCount++;
							Thread aThread = new Thread(new WorkerWrapper( signal, aWorker ));
							aThread.setName(aWorker.getThreadName());
							aThread.start();
							threadPool.add(aThread);
						}
					}
					//remove from waiting 
					workerItr.remove();
				} else if (StatusMeta.isFailed(getWorkerStatus(aWorker))) {
					workerItr.remove();
				}
			}
			if(waiting != null && waiting.size() > 0)
				signal.doWait();
			if(noNewThreadsOnFailure && getStatus( poolName ) == Status.FAILED) {
				LoggingTemplate.log(logger, LoggingTemplate.getPoolStopNextWorkerOnErrorLevel(), LoggingTemplate.getPoolStopNextWorkerOnError(), poolName);
				break;
			}
		}
		
		//Wait for all threads in the pool to complete
		long waitTime = logEnd(poolName,timer,true);		
		for(Thread aThread : threadPool) {
			while(aThread.isAlive()) {
				aThread.join(waitTime);
				waitTime = logEnd(poolName,timer,true);
			}
			threadMinus();
		}
		
		timer.stop();
		logEnd(poolName,timer, false);
		keys.remove(poolName);
		lastMsg.remove(poolName);
		lastWarn.remove(poolName);
		
		return this;
	}
	/***
	 * Used in conjunction with any of Async methods to block execution until the passed poolName completes or waitMilliseconds elapses. If waitMilliseconds = 0 then it will wait forever for the execution to complete.
	 * @param waitMilliseconds the maximum time this will wait for execution to complete. Pass if you don't want a maximum time.
	 * @param poolName The name of the pool you wish wait for.
	 * @return this - for method chaining
	 * @throws InterruptedException Thrown if the pool is still running after waitMilliseconds elapsed.
	 */
	public WorkerPool joinPool( int waitMilliseconds, String poolName ) throws InterruptedException {
		if(poolRunning.containsKey(poolName) && poolRunning.get(poolName).isAlive()){
			poolRunning.get(poolName).join(waitMilliseconds);
			if(poolRunning.get(poolName) != null && poolRunning.get(poolName).isAlive())
				throw new InterruptedException("Pool " + poolName + " is still running after waitMilliseconds elapsed") ;
		}
		return this;
	}
	/***
	 * Used in conjunction with any of Async methods to block execution until the passed poolNames completes or waitMilliseconds elapses. If waitMilliseconds = 0 then it will wait forever for the execution to complete.
	 * @param waitMilliseconds the maximum time this will wait for execution to complete. Pass if you don't want a maximum time.
	 * @param poolNames The name of the pools you wish wait for either as a list of parameters or a single array.
	 * @return this - for method chaining
	 * @throws InterruptedException Thrown if the pool is still running after waitMilliseconds elapsed.
	 */
	public WorkerPool joinPools(int waitMilliseconds, String ...poolNames) throws InterruptedException {
		if(poolNames != null && poolNames.length > 0) {
			Timer timer = new Timer().start();
			for(String aPool : poolNames) {
				if(waitMilliseconds != 0) {
					waitMilliseconds -= timer.getDuration();
					if(waitMilliseconds < 0)
						throw new InterruptedException("Maximum waitMilliseconds has elapsed");
				}
				joinPool( waitMilliseconds, aPool );
			}
		}
		return this;
	}
	
	private void waitForThreadPool(Signal signal, List<Thread> threadPool, String poolName, Timer timer) {
		//If thread pool is bigger than maxThreadCount check to see if threads can be pruned, if we can't then wait
		while(getThreadCount() >= maxThreadCount) {
			//Loop over threads, pruning if they're no longer alive.
			Iterator<Thread> threadItr = threadPool.iterator();
			while(threadItr.hasNext())
				if(!threadItr.next().isAlive()) {
					threadItr.remove();
					threadMinus();
				}

			if(getThreadCount() > maxThreadCount)
				signal.doWait();
		}
	}
	private Status getWorkerStatus(WorkerInterface worker) {
		if(worker.getStatus() == null)
			return Status.EMPTY;
		return worker.getStatus();
	}
	
	/***
	 * Gets the composite status of an entire pool.
	 * @param poolName the name of the pool you want to check
	 * @return the composite status of the pool
	 */
	public Status getStatus( String poolName ) {
		Status output = Status.EMPTY;
		int min = Integer.MAX_VALUE;
		for(WorkerInterface aWorker : pools.get(poolName)) {
			if(StatusMeta.getSeverity(getWorkerStatus(aWorker)) < min) {
				min = StatusMeta.getSeverity(getWorkerStatus(aWorker));
				output = getWorkerStatus(aWorker);
			}
		}
		return output;
	}
	/***
	 * Gets the composite status of the passed pools.
	 * @param poolNames the name of the pools you want to check
	 * @return the composite status of the pools
	 */
	public Status getStatus( String ...poolNames ) {
		Status output = Status.EMPTY;
		for(String aPoolName : poolNames) {
			Status aStatus = getStatus(aPoolName);
			if(output == null) {
				output = aStatus;
			} else if ( StatusMeta.getSeverity(aStatus) < StatusMeta.getSeverity(output) ) {
				output = aStatus;
			}
		}
		return output;
	}
	/***
	 * Gets the composite status of all pools.
	 * @return The composite status of all pools.
	 */
	public Status getAllStatus() {
		Set<String> statusList = pools.keySet();
		return getStatus(statusList.toArray(new String[statusList.size()]));
	}
	
	private String getFailedPools( String ...poolNames ) {
		StringBuilder output = new StringBuilder();
		First first = new First();
		for(String aPoolName : poolNames) {
			if(getStatus(aPoolName).equals(Status.FAILED)) {
				if(!first.first())
					output.append(", ");
				output.append(aPoolName);
			}
		}
		return output.toString();
	}
	
	private void logStart( String aKey ) {
		StringBuilder output = new StringBuilder();
		output.append("Staring Pool: ").append(aKey).append(". Containing: ");
		for(WorkerInterface aWorker : pools.get(aKey)) {
			output.append("\n\tWorker: ").append(aWorker.getThreadName());
			Set<String> pc = getCurrentPrecedenceConstraint( aWorker, aKey);
			if(pc.size() > 0) {
				output.append(" (Waiting on thread").append((pc.size() > 1) ? "s" : "").append(": ");
				First first = new First();
				for(String aConstraint : pc) {
					if(!first.first())
						output.append(", ");
					output.append(aConstraint);
				}
				output.append(")");
			}
		}
		LoggingTemplate.log(logger, LoggingTemplate.getPoolStartLevel(), output.toString());
	}
	
	private long logEnd( String aKey, Timer timer, boolean running) {
		Level level = Level.info;
		long waitTime = -1;
		if(running) {
			level = Level.off;
			if(timer.getSecondDuration() - lastWarn.get(aKey) > secondsBeforeWarn) {
				level = Level.warn;
				lastMsg.put(aKey, timer.getSecondDuration());
				lastWarn.put(aKey, timer.getSecondDuration());
			} else if(timer.getSecondDuration() - lastMsg.get(aKey) > secondsBetweenMsg) {
				level = Level.info;
				lastMsg.put(aKey, timer.getSecondDuration());
			}
			waitTime = secondsBetweenMsg - ( timer.getSecondDuration() - lastMsg.get(aKey) )+1;
			long warnTime = secondsBeforeWarn - ( timer.getSecondDuration() - lastWarn.get(aKey) )+1;
			if(waitTime > warnTime)
				waitTime = warnTime;
			if(level.equals(Level.off))
				return waitTime;
		}
		
		StringBuilder output = new StringBuilder();
		output.append("Pool: ").append(aKey);
		if(running) {
			output.append(" is still running. It has been running for ").append(timer.toString()).append(". Detail status: ");
		} else {
			output.append(". Completed with status: ").append(getStatus(aKey)).append(". It ran for: ").append(timer.toString()).append(". Detail stauts: ");
		}
		
		for(WorkerInterface aWorker : pools.get(aKey)) {
			output.append("\n\tWorker: ").append(aWorker.getThreadName()).append(" : ").append(getWorkerStatus(aWorker));
			Set<String> pc = getCurrentPrecedenceConstraint( aWorker, aKey);
			if(pc.size() > 0) {
				output.append(" (Waiting on thread").append((pc.size() > 1) ? "s" : "").append(": ");
				First first = new First();
				for(String aConstraint : pc) {
					if(!first.first())
						output.append(", ");
					output.append(aConstraint);
				}
				output.append(")");
			}
		}
		LoggingTemplate.log(logger, level, output.toString());
		return waitTime;
	}
	
	private Set<String> getCurrentPrecedenceConstraint(WorkerInterface toCheck, String aKey ){
		List<WorkerInterface> pool = pools.get(aKey);
		Set<String> output = new HashSet<>();
		
		if(toCheck.getPrecedenceConstraint() == null || toCheck.getPrecedenceConstraint().size() == 0)
			return output;
		for(String aConstraint : toCheck.getPrecedenceConstraint()) {
			for(WorkerInterface aWorker : pool) {
				if(aConstraint.equalsIgnoreCase(aWorker.getThreadName())) {
					if(StatusMeta.isOpen(getWorkerStatus(aWorker))) {
						output.add(aWorker.getThreadName());
					}
				}
			}
		}
		return output;
	}
}
