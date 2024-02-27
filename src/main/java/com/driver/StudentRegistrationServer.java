package com.driver;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StudentRegistrationServer {
	private static final int THREAD_POOL_SIZE = 5;
	private static final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	private static final CountDownLatch shutdownLatch = new CountDownLatch(1);

	public static void main(String[] args) {
		try {
			startServer();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			stopServer();
		}
	}

	private static void startServer() throws InterruptedException {
		System.out.println("Registration server started. Enter 'shutdown' to stop the server.");

		// Start the registration tasks
		for (int i = 1; i <= 10; i++) {
			final int studentId = i;  // Use a final variable
			executorService.submit(() -> registerStudent(studentId));
		}

		// Wait for the shutdown signal
		waitForShutdownSignal();
	}

	private static void registerStudent(int studentId) {
		// Simulate student registration task
		System.out.println("Student " + studentId + " registered.");
	}

	private static void waitForShutdownSignal() throws InterruptedException {
		Scanner scanner = new Scanner(System.in);
		while (true) {
			String userInput = scanner.nextLine().trim().toLowerCase();

			if ("shutdown".equals(userInput)) {
				System.out.println("Shutting down the server gracefully...");
				shutdownLatch.countDown(); // Release the latch to allow server shutdown
				break;
			} else {
				System.out.println("Unknown command. Enter 'shutdown' to stop the server.");
			}
		}

		scanner.close();
	}

	private static void stopServer() {
		try {
			executorService.shutdown();
			if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
				// Some tasks are still running after 10 seconds, force shutdown
				System.out.println("Forcing shutdown due to pending tasks...");
				executorService.shutdownNow();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}