package com.ross.scoreserver;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Level {
	private List<Score> scores;

	public Level() {
		scores = Collections.synchronizedList(new LinkedList<Score>());
	}

	public synchronized void addScore(Score score) {
		Score existingScore = findExistingUserScore(score);
		if (existingScore != null) {
			if (existingScore.score < score.score)
				scores.remove(existingScore);
			else
				return;
		}
		for (int i = 0; i < 15; i++) {
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
		if (scores.size() == 16) {
			scores.remove(15);
		}
	}

	public Score findExistingUserScore(Score score) {
		for (Score s : scores) {
			if (s.userId == score.userId) {
				return s;
			}
		}
		return null;
	}

	public String getHighScores() {
		StringBuffer highScores = new StringBuffer();
		for (int i = 0; i < scores.size(); i++) {
			highScores.append(scores.get(i).userId + "=" + scores.get(i).score);
			if (i != scores.size() - 1)
				highScores.append(",");
		}
		return highScores.toString();
	}
	
	public int getSize(){
		return scores.size();
	}
}
