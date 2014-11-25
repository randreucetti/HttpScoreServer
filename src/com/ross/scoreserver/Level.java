package com.ross.scoreserver;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Level class containing a Linked List of scores for that level, Linked List
 * size is capped at 15 as that's all that's ever used.
 * 
 * @author Ross Andreucetti
 * @since 25 Nov 2014
 *
 */
public class Level {
	private List<Score> scores;
	public static final int listSize = 15;

	public Level() {
		scores = Collections.synchronizedList(new LinkedList<Score>());
	}

	/**
	 * Score that will be added to list
	 * 
	 * @param score
	 *            Score to be added
	 */
	public synchronized void addScore(Score score) {
		// Firstly check to see if user already has a score
		Score existingScore = findExistingUserScore(score);
		if (existingScore != null) {
			if (existingScore.score < score.score) // is the new score better?
				scores.remove(existingScore);
			else
				return;
		}
		// insert the score at the appropriate position in queue
		for (int i = 0; i < listSize; i++) {
			if (i < scores.size()) {
				Score s = scores.get(i);
				if (s.score < score.score) {
					scores.add(i, score);
					break;
				}
			} else {
				scores.add(i, score);
				break;
			}

		}
		// keeps our list to the specified size
		if (scores.size() == (listSize + 1)) {
			scores.remove(listSize);
		}
	}

	/**
	 * Searches linkedList for a score with same UserId, Duplicates are
	 * pointless in this scenario
	 * 
	 * @param score
	 *            Score to be search for
	 * @return The existing score if found, otherwise null
	 */
	public Score findExistingUserScore(Score score) {
		for (Score s : scores) {
			if (s.userId == score.userId) {
				return s;
			}
		}
		return null;
	}

	/**
	 * 
	 * @return The highscores formatted as csv as required
	 */
	public String getHighScores() {
		StringBuffer highScores = new StringBuffer();
		for (int i = 0; i < scores.size(); i++) {
			highScores.append(scores.get(i).userId + "=" + scores.get(i).score);
			if (i != scores.size() - 1)
				highScores.append(",");
		}
		return highScores.toString();
	}

	/**
	 * Used for unit testing
	 * 
	 * @return Size of the LinkedList
	 */
	public int getSize() {
		return scores.size();
	}
}
