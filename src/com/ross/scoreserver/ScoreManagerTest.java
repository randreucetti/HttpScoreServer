package com.ross.scoreserver;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;


public class ScoreManagerTest {
	@Test
	public void testLogin() {
		ScoreManager manager = new ScoreManager(false);
		String sessionKey = manager.login(12345);
		assertTrue(sessionKey.matches("[A-Z]{7}"));
		assertTrue(manager.getUsers().size() == 1);
		assertTrue(manager.getActiveSessions().size() == 1);
	}

	@Test
	public void testPostScoreEmpty(){
		ScoreManager manager = new ScoreManager(false);
		manager.postScore(1234, "ABCDEFG", 50000);
		assertNull(manager.getLevels().get(1234));	//level should not have been created as sessionKey is not valid
	}

	@Test
	public void testPostScore() {
		ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap<Integer, User>();
		User user = new User(23423);
		users.put(23423, user);
		ConcurrentHashMap<String, User> activeSessions = new ConcurrentHashMap<String, User>();
		activeSessions.put("ABCDEFG", user);
		ScoreManager manager = new ScoreManager(users, activeSessions,
				new ConcurrentHashMap<Integer, Level>());
		manager.postScore(1234, "ABCDEFG", 50000);
		assertTrue(manager.getLevels().get(1234).getSize() == 1);
	}

	@Test
	public void testGetHighscore() {
		ConcurrentHashMap<Integer, Level> levels = new ConcurrentHashMap<Integer, Level>();
		Level level = new Level();
		level.addScore(new Score(1, 542352));
		level.addScore(new Score(2, 78589));
		level.addScore(new Score(3, 6433));
		level.addScore(new Score(4, 2542));
		level.addScore(new Score(5, 7644));
		level.addScore(new Score(6, 764));
		level.addScore(new Score(7, 23455));
		level.addScore(new Score(8, 245));
		level.addScore(new Score(9, 6356));
		level.addScore(new Score(10, 154365));
		level.addScore(new Score(11, 767));
		level.addScore(new Score(12, 6563));
		level.addScore(new Score(13, 6536));
		level.addScore(new Score(14, 452));
		level.addScore(new Score(15, 12));
		level.addScore(new Score(16, 54));
		level.addScore(new Score(17, 765));
		level.addScore(new Score(18, 500));
		level.addScore(new Score(18, 1000));

		levels.put(1234, level);
		ScoreManager manager = new ScoreManager(null, null, levels);
		String highScores = manager.getHighScore(1234);
		assertTrue(manager.getLevels().get(1234).getSize() == 15);
		assertNotNull(highScores);


	}
}
