package com.ross.scoreserver;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;

import org.easymock.EasyMock;
import org.junit.Test;

import com.sun.net.httpserver.HttpExchange;

public class HttpScoreServerTest {

	@Test
	public void testLogin() throws URISyntaxException, IOException {

		ScoreHandler handler = new ScoreHandler();
		HttpExchange exchange = EasyMock.createStrictMock(HttpExchange.class);
		OutputStream os = EasyMock.createStrictMock(OutputStream.class);
		EasyMock.expect(exchange.getRequestURI()).andReturn(
				new URI("/23423/login"));
		exchange.sendResponseHeaders(200, 7);
		EasyMock.expectLastCall();
		EasyMock.expect(exchange.getResponseBody()).andReturn(os);
		EasyMock.replay(exchange);
		handler.handle(exchange);
		EasyMock.verify(exchange);
		assertTrue(handler.getUsersSize() == 1);
	}

	@Test
	public void testBadUri() throws URISyntaxException, IOException {
		ScoreHandler handler = new ScoreHandler();
		HttpExchange exchange = EasyMock.createStrictMock(HttpExchange.class);
		OutputStream os = EasyMock.createStrictMock(OutputStream.class);
		EasyMock.expect(exchange.getRequestURI()).andReturn(
				new URI("/2342fsd3/logiout"));
		exchange.sendResponseHeaders(400, 11);
		EasyMock.expectLastCall();
		EasyMock.expect(exchange.getResponseBody()).andReturn(os);
		EasyMock.replay(exchange);
		handler.handle(exchange);
		EasyMock.verify(exchange);
	}

	@Test
	public void testPostScoreEmpty() throws URISyntaxException, IOException {
		ScoreHandler handler = new ScoreHandler();
		String fakeInput = "50000";
		InputStream is = new ByteArrayInputStream(fakeInput.getBytes());
		HttpExchange exchange = EasyMock.createStrictMock(HttpExchange.class);
		EasyMock.expect(exchange.getRequestURI())
				.andReturn(new URI("/1234/score?sessionkey=ABCDEFG")).times(2);
		EasyMock.expect(exchange.getRequestBody()).andReturn(is);
		EasyMock.replay(exchange);
		handler.handle(exchange);
		EasyMock.verify(exchange);
	}

	@Test
	public void testPostScore() throws IOException, URISyntaxException {
		Hashtable<Integer, User> users = new Hashtable<Integer, User>();
		User user = new User(23423);
		users.put(23423, user);
		Hashtable<String, User> activeSessions = new Hashtable<String, User>();
		activeSessions.put("ABCDEFG", user);
		ScoreHandler handler = new ScoreHandler(users, activeSessions,
				new Hashtable<Integer, Level>());
		String fakeInput = "50000";
		InputStream is = new ByteArrayInputStream(fakeInput.getBytes());
		HttpExchange exchange = EasyMock.createStrictMock(HttpExchange.class);
		EasyMock.expect(exchange.getRequestURI())
				.andReturn(new URI("/1234/score?sessionkey=ABCDEFG")).times(2);
		EasyMock.expect(exchange.getRequestBody()).andReturn(is);
		EasyMock.replay(exchange);
		handler.handle(exchange);
		EasyMock.verify(exchange);
	}

	@Test
	public void testGetHighscore() throws URISyntaxException, IOException {
		Hashtable<Integer, User> users = new Hashtable<Integer, User>();
		User user = new User(23423);
		users.put(23423, user);
		User user2 = new User(23456);
		users.put(23456, user2);
		Hashtable<String, User> activeSessions = new Hashtable<String, User>();
		activeSessions.put("ABCDEFG", user);
		activeSessions.put("BCDEFGH", user2);
		Hashtable<Integer, Level> levels = new Hashtable<Integer, Level>();
		Level level = new Level();
		level.addScore(new Score(50000, 234323));
		level.addScore(new Score(50000, 234231));
		level.addScore(new Score(30000, 23423));
		level.addScore(new Score(3, 23424));
		level.addScore(new Score(13435423, 23423));
		level.addScore(new Score(64536, 23429));
		level.addScore(new Score(50000, 234328));
		level.addScore(new Score(50000, 234237));
		level.addScore(new Score(30000, 23426));
		level.addScore(new Score(3, 23425));
		level.addScore(new Score(13435423, 23424));
		level.addScore(new Score(64536, 123423));
		level.addScore(new Score(50000, 2234323));
		level.addScore(new Score(50000, 3234231));
		level.addScore(new Score(30000, 423423));
		level.addScore(new Score(3, 523424));
		level.addScore(new Score(13435423, 623423));
		level.addScore(new Score(64536, 723423));
		level.addScore(new Score(64536, 7233423));
		levels.put(1234, level);
		ScoreHandler handler = new ScoreHandler(users, activeSessions, levels);
		HttpExchange exchange = EasyMock.createStrictMock(HttpExchange.class);
		OutputStream os = EasyMock.createStrictMock(OutputStream.class);
		EasyMock.expect(exchange.getRequestURI()).andReturn(
				new URI("/1234/highscorelist"));
		exchange.sendResponseHeaders(200, 202);
		EasyMock.expect(exchange.getResponseBody()).andReturn(os);
		EasyMock.expectLastCall();
		EasyMock.replay(exchange);

		handler.handle(exchange);
		EasyMock.verify(exchange);
		
		System.out.println(handler.getHighScore(1234));

	}
}
