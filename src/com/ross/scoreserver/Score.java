package com.ross.scoreserver;

public class Score implements Comparable<Score> {
	int score;
	int userId;
	
	public Score(int score, int userId) {
		super();
		this.score = score;
		this.userId = userId;
	}
	
	@Override
	public int compareTo(Score o) {
		return (o.score - score);
	}

	@Override
	public String toString() {
		return userId + "=" + score;
	}
}
