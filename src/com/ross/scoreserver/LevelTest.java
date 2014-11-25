package com.ross.scoreserver;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Some unit tests designed for the Level class
 * 
 * @author Ross Andreucetti
 * @since 25 Nov 2014
 *
 */
public class LevelTest {

	@Test
	public void testAddScore() {
		Level level = new Level();
		assertTrue(level.getSize() == 0);
		level.addScore(new Score(1, 5000));
		assertTrue(level.getSize() == 1);
	}

	@Test
	public void testScoresCapped() {
		Level level = new Level();
		assertTrue(level.getSize() == 0);
		for (int i = 0; i < 20; i++) {
			level.addScore(new Score(i, 5000));
		}
		assertTrue(level.getSize() == 15);
	}

	@Test
	public void testFindExistingUser() {
		Level level = new Level();
		level.addScore(new Score(1, 5000));
		assertNotNull(level.findExistingUserScore(new Score(1, 2000)));
	}
}
