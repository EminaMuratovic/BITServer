package ba.BITCamp.Server;

import java.io.FileNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ba.BITCamp.logger.Logger;

public class ConnectionListener {
	private static final int port = 8080;

	public static void main(String[] args) {
		ExecutorService pool = Executors.newFixedThreadPool(10);
		HashMap<String, String> logs = new HashMap<String, String>();
		logs.put("application", "application");
		logs.put("warning", "warning");
		logs.put("error", "error");

		try {
			new Logger(logs);
		} catch (FileNotFoundException e1) {
			System.out.println("Could not initialize logger.");
			System.exit(1);
		}

		try {
			ServerSocket server = new ServerSocket(port);
			while (true) {
				Socket client = server.accept();
				Logger.log("application", client.getInetAddress()
						.getHostAddress() + " just connected");
				pool.submit(new Connection(client));

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
