package com.vishal.mockapi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MockapiApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(MockapiApplication.class, args);
			System.out.println(
					"███    ███  ██████   ██████ ██   ██      █████  ██████  ██ \n" +
							"████  ████ ██    ██ ██      ██  ██      ██   ██ ██   ██ ██ \n" +
							"██ ████ ██ ██    ██ ██      █████       ███████ ██████  ██ \n" +
							"██  ██  ██ ██    ██ ██      ██  ██      ██   ██ ██      ██ \n" +
							"██      ██  ██████   ██████ ██   ██     ██   ██ ██      ██ \n" +
							"                                                           \n");
		} catch (Exception e) {
			try {
				if (e.getMessage() != null && !e.getMessage().isBlank()) {
					String timestamp = LocalDateTime.now().format(new DateTimeFormatterBuilder()
							.appendPattern("yyyy-MM-dd_HH-mm-ss").toFormatter()).toString();
					java.io.FileWriter fw = new java.io.FileWriter(timestamp + "_error.log", true);
					java.io.PrintWriter pw = new java.io.PrintWriter(fw);
					pw.println("Exception occurred: " + e.getMessage());
					e.printStackTrace(pw);
				}
			} catch (java.io.IOException ioEx) {
				System.err.println("Failed to write to log file: " + ioEx.getMessage());
			}
			// e.printStackTrace();
		}
	}

}
