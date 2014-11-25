package com.ross.scoreserver;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ScoreManager class holds all data around the ScoreServer system
 * ConcurrentHashMaps used to ensure ThreadSafety.
 * 
 * @author Ross Andreucetti
 * @since 25 Nov 2014
 *
 */
public class ScoreManager {
	public static final int timeToLive = 600000; // 10 minutes
	private boolean threadedCleanup; // if enabled a thread removes stale
										// sessions, if not lazy removal is used
	private ConcurrentHashMap<Integer, User> users; // users are stored hear
													// "permanently"
	private ConcurrentHashMap<String, User> activeSessions; // active sessions
															// in here
	private ConcurrentHashMap<Integer, Level> levels; // levels are stored here

	/**
	 * 
	 * @param threadedCleanup
	 *            whether thread should remove sessions or use lazy removal
	 */
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

	/**
	 * Dependency injection used to allow unit testing
	 * 
	 * @param users
	 * @param activeSessions
	 * @param levels
	 */
	public ScoreManager(ConcurrentHashMap<Integer, User> users, ConcurrentHashMap<String, User> activeSessions,
			ConcurrentHashMap<Integer, Level> levels) {
		super();
		this.users = users;
		this.activeSessions = activeSessions;
		this.levels = levels;
	}

	/**
	 * Used to login or create a new user
	 * 
	 * @param userId
	 *            The user to be logged in
	 * @return
	 */
	public String login(int userId) {
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

	/**
	 * Posts a new score to a particular level
	 * 
	 * @param levelId
	 * @param sessionId
	 * @param score
	 */
	public void postScore(int levelId, String sessionId, int score) {
		if (levels.containsKey(levelId)) {
			Level existingLevel = levels.get(levelId);
			User theUser = activeSessions.get(sessionId);
			if (!threadedCleanup) {
				if ((System.currentTimeMillis() - theUser.getLastActive()) > timeToLive) {
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
			if (!threadedCleanup) {
				if ((System.currentTimeMillis() - theUser.getLastActive()) > timeToLive) {
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

	/**
	 * Retrieves highscores in csv
	 * 
	 * @param levelId
	 * @return csv string
	 */
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
