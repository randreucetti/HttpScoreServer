package com.ross.scoreserver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.easymock.EasyMock;
import org.junit.Test;

import com.sun.net.httpserver.HttpExchange;

/**
 * Unit tests around the ScoreHandler class, Checks good/bad URLs etc
 * 
 * @author Ross Andreucetti
 * @since 25 Nov 2014
 *
 */
public class ScoreHandlerTest {

	@Test
	public void testGoodLogin() throws URISyntaxException, IOException {
		ScoreManager manager = EasyMock.createStrictMock(ScoreManager.class);
		EasyMock.expect(manager.login(EasyMock.anyInt())).andReturn("ABCDEFG");
		ScoreHandler handler = new ScoreHandler(manager);
		HttpExchange exchange = EasyMock.createStrictMock(HttpExchange.class);
		OutputStream os = EasyMock.createStrictMock(OutputStream.class);
		EasyMock.expect(exchange.getRequestURI()).andReturn(new URI("/23423/login"));
		exchange.sendResponseHeaders(200, 7);
		EasyMock.expectLastCall();
		EasyMock.expect(exchange.getResponseBody()).andReturn(os);
		EasyMock.replay(exchange);
		EasyMock.replay(manager);
		handler.handle(exchange);
		EasyMock.verify(exchange);
		EasyMock.verify(manager);
	}

	@Test
	public void testBadLogin() throws URISyntaxException, IOException {
		ScoreHandler handler = new ScoreHandler(false);
		HttpExchange exchange = EasyMock.createStrictMock(HttpExchange.class);
		OutputStream os = EasyMock.createStrictMock(OutputStream.class);
		EasyMock.expect(exchange.getRequestURI()).andReturn(new URI("/2342fsd3/login"));
		exchange.sendResponseHeaders(400, 11);
		EasyMock.expectLastCall();
		EasyMock.expect(exchange.getResponseBody()).andReturn(os);
		EasyMock.replay(exchange);
		handler.handle(exchange);
		EasyMock.verify(exchange);
	}

	@Test
	public void testGoodPostScore() throws URISyntaxException, IOException {
		ScoreManager manager = EasyMock.createStrictMock(ScoreManager.class);
		manager.postScore(1234, "ABCDEFG", 50000);
		EasyMock.expectLastCall();
		ScoreHandler handler = new ScoreHandler(manager);
		String fakeInput = "50000";
		InputStream is = new ByteArrayInputStream(fakeInput.getBytes());
		HttpExchange exchange = EasyMock.createStrictMock(HttpExchange.class);
		EasyMock.expect(exchange.getRequestURI()).andReturn(new URI("/1234/score?sessionkey=ABCDEFG")).times(2);
		EasyMock.expect(exchange.getRequestBody()).andReturn(is);
		EasyMock.replay(exchange);
		EasyMock.replay(manager);
		handler.handle(exchange);
		EasyMock.verify(exchange);
		EasyMock.verify(manager);
	}

	@Test
	public void testBadPostScore() throws URISyntaxException, IOException {
		ScoreHandler handler = new ScoreHandler(false);
		HttpExchange exchange = EasyMock.createStrictMock(HttpExchange.class);
		OutputStream os = EasyMock.createStrictMock(OutputStream.class);
		EasyMock.expect(exchange.getRequestURI()).andReturn(new URI("/1234/score?sessionkey=AB1DEFG"));
		exchange.sendResponseHeaders(400, 11);
		EasyMock.expect(exchange.getResponseBody()).andReturn(os);
		EasyMock.expectLastCall();
		EasyMock.replay(exchange);
		handler.handle(exchange);
		EasyMock.verify(exchange);
	}

	@Test
	public void testGoodGetHighScore() throws URISyntaxException, IOException {
		ScoreManager manager = EasyMock.createStrictMock(ScoreManager.class);
		EasyMock.expect(manager.getHighScore(1234)).andReturn("1234=50000,2345=40000");
		ScoreHandler handler = new ScoreHandler(manager);
		HttpExchange exchange = EasyMock.createStrictMock(HttpExchange.class);
		OutputStream os = EasyMock.createStrictMock(OutputStream.class);
		EasyMock.expect(exchange.getRequestURI()).andReturn(new URI("/1234/highscorelist"));
		exchange.sendResponseHeaders(200, 21);
		EasyMock.expect(exchange.getResponseBody()).andReturn(os);
		EasyMock.expectLastCall();
		EasyMock.replay(exchange);
		EasyMock.replay(manager);
		handler.handle(exchange);
		EasyMock.verify(exchange);
		EasyMock.verify(manager);
	}

	@Test
	public void testBadGetHighScore() throws URISyntaxException, IOException {
		ScoreHandler handler = new ScoreHandler(false);
		HttpExchange exchange = EasyMock.createStrictMock(HttpExchange.class);
		OutputStream os = EasyMock.createStrictMock(OutputStream.class);
		EasyMock.expect(exchange.getRequestURI()).andReturn(new URI("/12a4/highscorelist"));
		exchange.sendResponseHeaders(400, 11);
		EasyMock.expect(exchange.getResponseBody()).andReturn(os);
		EasyMock.expectLastCall();
		EasyMock.replay(exchange);
		handler.handle(exchange);
		EasyMock.verify(exchange);
	}
}
