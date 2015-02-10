package ba.BITCamp.Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import ba.BITCamp.logger.Logger;

public class Connection implements Runnable {

	private Socket client;

	public Connection(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {
		BufferedReader read = null;
		PrintStream write = null;

		try {
			read = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			write = new PrintStream(client.getOutputStream());
		} catch (IOException e) {
			Logger.log("error", e.getMessage());
			closeClient();
			return;
		}
		String str = null;
		try {
			while ((str = read.readLine()) != null) {
				if (str.contains("GET") || str.isEmpty()) {
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

			if (!str.contains("GET")) {
				Logger.log("warning", "Was not GET request");
				Response.error(write, "Invalid request");
				closeClient();
				return;
			}
			String fileName = getFileName(str);
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(fileName);
			} catch (FileNotFoundException e) {
				Response.error(write, "This is not the page you are looking for");
				Logger.log("warning", "File not found" + e.getMessage());
				closeClient();
				return;
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String fileLine = "";
			StringBuilder sb = new StringBuilder();
			
			try {
				while ((fileLine = br.readLine()) != null)
					sb.append(fileLine);
			} catch (IOException e) {
				Response.serverError(write, "A well trained group of monkeys is trying to fix the problem.");
				Logger.log("error", e.getMessage());
				closeClient();
				return;
			}
			Response.ok(write,sb.toString());
			closeClient();

	}

	private String getFileName(String request) {
		String[] parts = request.split(" ");
		String fileName = null;
		for (int i = 0; i < parts.length; i++)
			if (parts[i].equals("GET")) {
				fileName = parts[i + 1];
				break;
			}
		String basePath = "." + File.separator + "html" + File.separator;
		if(fileName == null || fileName.equals("/"))
			return basePath + "index.html";
		
		if(!fileName.contains("."))
			fileName += ".html";
		return basePath + fileName;
		

	}
	
	private void closeClient() {
		try {
			client.close();
		} catch (IOException e) {
			Logger.log("warning", e.getMessage());
		}
	}

}
