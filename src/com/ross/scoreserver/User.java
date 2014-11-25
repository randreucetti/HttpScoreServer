package com.ross.scoreserver;

/**
 * User class, simple
 * 
 * @author Ross Andreucetti
 * @since 25 Nov 2014
 *
 */
public class User {
	private long lastActive;
	private int userId;

	public User(int userId) {
		lastActive = System.currentTimeMillis();
		this.userId = userId;
	}

	public long getLastActive() {
		return lastActive;
	}

	public void update() {
		this.lastActive = System.currentTimeMillis();
		;
	}

	public int getUserId() {
		return userId;
	}
}
