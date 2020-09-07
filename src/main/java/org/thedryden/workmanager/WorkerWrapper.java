package org.thedryden.workmanager;

class WorkerWrapper implements Runnable {
	private Signal signal;
	private WorkerInterface worker;
	
	public WorkerWrapper(Signal signal, WorkerInterface worker) {
		this.signal = signal;
		this.worker = worker;
	}

	@Override
	public void run() {
		worker.run();
		signal.doNotify();
	}
}
