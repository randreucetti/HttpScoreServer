package com.ross.scoreserver;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A simple thread that monitors the active sessions and removes any older than
 * the TTL
 * 
 * @author Ross Andreucetti
 * @since 25 Nov 2014
 *
 */
public class CleanupRunnable implements Runnable {

	private ConcurrentHashMap<String, User> sessionsToMonitor;
	private long ttl;

	public CleanupRunnable(ConcurrentHashMap<String, User> sessionsToMonitor, long ttl) {
		this.sessionsToMonitor = sessionsToMonitor;
		this.ttl = ttl;
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			removeStaleSessions();
		}

	}

	/**
	 * removes and stale sessions from the ConcurrentHashMap
	 */
	private void removeStaleSessions() {

		Iterator<Map.Entry<String, User>> it = sessionsToMonitor.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, User> entry = it.next();
			if ((System.currentTimeMillis() - entry.getValue().getLastActive()) > ttl) {
				it.remove();
			}
		}

	}

}
