package com.ross.scoreserver;

import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

public class HttpScoreServer {

	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		server.createContext("/", new ScoreHandler());
		server.setExecutor(null); // creates a default executor
		server.start();
	}
}
