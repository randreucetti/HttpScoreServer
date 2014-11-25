package com.ross.scoreserver;

public class Score {
	int userId;
	int score;
	
	public Score(int userId, int score) {
		super();
		this.score = score;
		this.userId = userId;
	}

	@Override
	public String toString() {
		return userId + "=" + score;
	}
}
