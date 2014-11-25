package com.ross.scoreserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * ScoreHandler class handles incoming HTTP request and sends them to the
 * manager using the appropriate function. Bad URLs and invalid parameters are
 * also checked here
 * 
 * @author Ross Andreucetti
 * @since 25 Nov 2014
 *
 */
public class ScoreHandler implements HttpHandler {

	private ScoreManager manager;

	public ScoreHandler(boolean threadedCleanup) {
		super();
		manager = new ScoreManager(threadedCleanup);
	}

	public ScoreHandler(ScoreManager manager) {
		super();
		this.manager = manager;

	}

	/**
	 * Used for processing incoming HTTP requests REGEXs are used to validate
	 * URLs
	 */
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

	/**
	 * Handles login mechancism
	 * 
	 * @param t
	 * @param uri
	 * @throws IOException
	 */
	private void handleLogin(HttpExchange t, String uri) throws IOException {
		int userId = Integer.parseInt(uri.split("/")[1]);
		String response = manager.login(userId);
		t.sendResponseHeaders(200, response.length());
		OutputStream os = t.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	/**
	 * Handles post score mechanism
	 * 
	 * @param t
	 * @param uri
	 * @throws IOException
	 */
	private void handlePostScore(HttpExchange t, String uri) throws IOException {
		String param = t.getRequestURI().getQuery();
		int levelId = Integer.parseInt(uri.split("/")[1]);
		String sessionKey = param.substring(param.length() - 7);
		InputStream is = t.getRequestBody();
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		int score = Integer.parseInt(in.readLine());
		manager.postScore(levelId, sessionKey, score);
	}

	/**
	 * Handles high score retrieval
	 * 
	 * @param t
	 * @param uri
	 * @throws IOException
	 */
	private void handleGetHighScore(HttpExchange t, String uri) throws IOException {
		int levelId = Integer.parseInt(uri.split("/")[1]);
		String response = manager.getHighScore(levelId);
		t.sendResponseHeaders(200, response.length());
		OutputStream os = t.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}
