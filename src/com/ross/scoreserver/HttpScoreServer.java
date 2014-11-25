package com.ross.scoreserver;

import java.net.InetSocketAddress;
import java.util.Random;

import com.sun.net.httpserver.HttpServer;

/**
 * Houses our main method which will be used to start the server. Currently the
 * default exexcutor is used, this could be changed for larger applications
 * 
 * @author Ross Andreucetti
 * @since 25 Nov 2014
 *
 */
public class HttpScoreServer {

	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		server.createContext("/", new ScoreHandler(false));
		// creates a default executor, can be changed to make multithreaded
		server.setExecutor(null);
		// server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(4));
		server.start();

	}

	/**
	 * Previously used for some functional and stress testing
	 */
	public static void testSampleUsage() {
		int numIterations = 100000;
		String[] sessionkeys = new String[numIterations];
		ScoreManager manager = new ScoreManager(false);
		Random r = new Random();

		long startTime = System.currentTimeMillis();
		for (int i = 0; i < numIterations; i++) {
			sessionkeys[i] = manager.login(r.nextInt(1000));
			manager.postScore(r.nextInt(10) + 1, sessionkeys[i], r.nextInt(50000));
		}
		long stopTime = System.currentTimeMillis();
		System.out.println(stopTime - startTime + "ms");

		for (int i = 0; i < 10; i++) {
			System.out.println(manager.getHighScore(i));
		}
	}
}
