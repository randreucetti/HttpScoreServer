package com.ross.scoreserver;

import java.util.concurrent.ConcurrentHashMap;


public class ScoreManager {
	public static final int timeToLive = 600000;
	private boolean threadedCleanup; // if enabled a thread removes stale
										// sessions, if not lazy removal is used
	private ConcurrentHashMap<Integer, User> users;
	private ConcurrentHashMap<String, User> activeSessions;
	private ConcurrentHashMap<Integer, Level> levels;

	public ScoreManager(boolean threadedCleanup) {
		super();
		this.users = new ConcurrentHashMap<Integer, User>();
		this.activeSessions = new ConcurrentHashMap<String, User>();
		this.levels = new ConcurrentHashMap<Integer, Level>();
		this.threadedCleanup = threadedCleanup;
		if (threadedCleanup) {
			new Thread(new CleanupRunnable(activeSessions, timeToLive)).start();
		}
	}

	public ScoreManager(ConcurrentHashMap<Integer, User> users,
			ConcurrentHashMap<String, User> activeSessions,
			ConcurrentHashMap<Integer, Level> levels) {
		super();
		this.users = users;
		this.activeSessions = activeSessions;
		this.levels = levels;
	}

	public synchronized String login(int userId) {
		if (users.contains(userId)) { // existing user
			User existingUser = users.get(userId);
			String sessionkey = StringUtils.randomString(7);
			activeSessions.put(sessionkey, existingUser);
			existingUser.update();
			return sessionkey;
		} else { // new user
			User newUser = new User(userId);
			users.put(userId, newUser);
			String sessionkey = StringUtils.randomString(7);
			activeSessions.put(sessionkey, newUser);
			return sessionkey;
		}
	}

	public void postScore(int levelId, String sessionId, int score) {
		if (levels.containsKey(levelId)) {
			Level existingLevel = levels.get(levelId);
			User theUser = activeSessions.get(sessionId);
			if(!threadedCleanup){
				if((System.currentTimeMillis() - theUser.getLastActive()) > timeToLive){
					activeSessions.remove(sessionId);
					return;
				}
			}
			if (theUser != null) {
				theUser.update();
				Score newScore = new Score(theUser.getUserId(), score);
				existingLevel.addScore(newScore);
			}

		} else {
			Level newLevel = new Level();
			User theUser = activeSessions.get(sessionId);
			if(!threadedCleanup){
				if((System.currentTimeMillis() - theUser.getLastActive()) > timeToLive){
					activeSessions.remove(sessionId);
					return;
				}
			}
			if (theUser != null) {
				theUser.update();
				Score newScore = new Score(theUser.getUserId(), score);
				newLevel.addScore(newScore);
				levels.put(levelId, newLevel);
			}
		}
	}

	public String getHighScore(int levelId) {
		Level level = levels.get(levelId);
		if (level != null) {
			return level.getHighScores();
		} else {
			return "";
		}
	}

	public ConcurrentHashMap<Integer, User> getUsers() {
		return users;
	}

	public void setUsers(ConcurrentHashMap<Integer, User> users) {
		this.users = users;
	}

	public ConcurrentHashMap<String, User> getActiveSessions() {
		return activeSessions;
	}

	public void setActiveSessions(ConcurrentHashMap<String, User> activeSessions) {
		this.activeSessions = activeSessions;
	}

	public ConcurrentHashMap<Integer, Level> getLevels() {
		return levels;
	}

	public void setLevels(ConcurrentHashMap<Integer, Level> levels) {
		this.levels = levels;
	}

}
