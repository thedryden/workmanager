package org.thedryden.workmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/***
 * Used to manage signaling when a worker is complete. Used by WorkerPool to manage threads.
 * @author thedr
 *
 */
class Signal {
	private Logger logger;
	private SignalObj mySignalObj;
	private boolean wasSignalled;

	public Signal() {
		logger = LoggerFactory.getLogger(this.getClass());
		mySignalObj = new SignalObj();
		wasSignalled = false;
	}

	public void doWait() {
		synchronized (mySignalObj) {
			if (!wasSignalled) {
				try {
					//Never wait longer than 30 seconds, just in case.
					mySignalObj.wait(30_000);
				} catch (InterruptedException e) {
					logger.error("An error occured while tying to call wait: {}", e);
				}
			}
			// clear signal and continue running.
			wasSignalled = false;
		}
	}

	public void doNotify() {
		synchronized (mySignalObj) {
			wasSignalled = true;
			mySignalObj.notify();
		}
	}
}
