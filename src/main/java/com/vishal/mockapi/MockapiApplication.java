package com.vishal.mockapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MockapiApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(MockapiApplication.class, args);
		} catch (Exception e) {
			// System.err.println("ClassNotFoundException occurred: " + e.getMessage());
			try (java.io.FileWriter fw = new java.io.FileWriter("error.log", true);
					java.io.PrintWriter pw = new java.io.PrintWriter(fw)) {
				pw.println("ClassNotFoundException occurred: " + e.getMessage());
				e.printStackTrace(pw);
			} catch (java.io.IOException ioEx) {
				// System.err.println("Failed to write to log file: " + ioEx.getMessage());
			}
			// e.printStackTrace();
		}
		System.out.println(
				"███    ███  ██████   ██████ ██   ██      █████  ██████  ██ \n" +
						"████  ████ ██    ██ ██      ██  ██      ██   ██ ██   ██ ██ \n" +
						"██ ████ ██ ██    ██ ██      █████       ███████ ██████  ██ \n" +
						"██  ██  ██ ██    ██ ██      ██  ██      ██   ██ ██      ██ \n" +
						"██      ██  ██████   ██████ ██   ██     ██   ██ ██      ██ \n" +
						"                                                           \n");
	}

}
