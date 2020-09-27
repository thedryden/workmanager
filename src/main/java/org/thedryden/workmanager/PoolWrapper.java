package org.thedryden.workmanager;

class PoolWrapper implements Runnable  {
	private WorkerPool source;
	private String poolName;
	
	
	public PoolWrapper(WorkerPool source, String poolName) {
		super();
		this.source = source;
		this.poolName = poolName;
	}


	@Override
	public void run() {
		try {
			source.startOnePool(poolName, false);
		} catch (InterruptedException | DuplicateThreadNameException | CircularPrecedenceConstraintException | AlreadyRunningException e) {
			source.logger.error("Pool {}, failed with the following error: {}", poolName, e);
		}
	}
}
