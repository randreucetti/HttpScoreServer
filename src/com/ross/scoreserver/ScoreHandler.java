package com.ross.scoreserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Random;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ScoreHandler implements HttpHandler {

	private Hashtable<Integer, User> users;
	private Hashtable<String, User> activeSessions;
	private Hashtable<Integer, Level> levels;

	public ScoreHandler() {
		users = new Hashtable<Integer, User>();
		activeSessions = new Hashtable<String, User>();
		levels = new Hashtable<Integer, Level>();
	}
	
	public ScoreHandler(Hashtable<Integer, User> users,
			Hashtable<String, User> activeSessions,
			Hashtable<Integer, Level> levels) {
		super();
		this.users = users;
		this.activeSessions = activeSessions;
		this.levels = levels;
	}

	@Override
	public void handle(HttpExchange t) throws IOException {
		String request = t.getRequestURI().toString();
		if (request.matches("/[0-9]{1,10}/login")) {
			handleLogin(t, request);
		} else if (request.matches("/[0-9]{1,10}/score\\?sessionkey=[A-Z]{7}")) {
			handlePostScore(t, request);
		} else if (request.matches("/[0-9]{1,10}/highscorelist")) {
			handleGetHighScore(t, request);
		} else {
			String response = "Bad request";
			t.sendResponseHeaders(400, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}

	}

	private void handleLogin(HttpExchange t, String uri) throws IOException {
		int userId = Integer.parseInt(uri.split("/")[1]);
		String response = login(userId);
		t.sendResponseHeaders(200, response.length());
		OutputStream os = t.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	private void handlePostScore(HttpExchange t, String uri) throws IOException {
		String param = t.getRequestURI().getQuery();
		int levelId = Integer.parseInt(uri.split("/")[1]);
		String sessionKey = param.substring(param.length() - 7);
		InputStream is = t.getRequestBody();
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		int score = Integer.parseInt(in.readLine());
		postScore(levelId, sessionKey, score);

	}

	private void handleGetHighScore(HttpExchange t, String uri)
			throws IOException {
		int levelId = Integer.parseInt(uri.split("/")[1]);
		String response = getHighScore(levelId);
		t.sendResponseHeaders(200, response.length());
		OutputStream os = t.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
	
	

	private synchronized String login(int userId) {
		if (users.contains(userId)) { // existing user
			User existingUser = users.get(userId);
			String sessionkey = randomString(7);
			activeSessions.put(sessionkey, existingUser);
			existingUser.update();
			return sessionkey;
		} else { // new user
			User newUser = new User(userId);
			users.put(userId, newUser);
			String sessionkey = randomString(7);
			activeSessions.put(sessionkey, newUser);
			return sessionkey;
		}
	}

	private void postScore(int levelId, String sessionId, int score) {
		if (levels.contains(levelId)) {
			Level existingLevel = levels.get(levelId);
			User theUser = activeSessions.get(sessionId);
			if (theUser != null) {
				Score newScore = new Score(score, theUser.getUserId());
				existingLevel.addScore(newScore);
			}

		} else {
			Level newLevel = new Level();
			User theUser = activeSessions.get(sessionId);
			if (theUser != null) {
				Score newScore = new Score(score, theUser.getUserId());
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

	static final String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static Random rnd = new Random();

	public String randomString(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}
	


	public int getUsersSize(){
		return users.size();
	}
	
	public int getActiveSessionsSize(){
		return activeSessions.size();
	}

}
