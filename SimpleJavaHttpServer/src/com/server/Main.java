package com.server;

public class Main {
	public static int port = 9000;
	public static void main(String[] args) {
		// start https server
		SimpleHttpsServer httpsServer = new SimpleHttpsServer();
		httpsServer.Start(port);
	}
}
