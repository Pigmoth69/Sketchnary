package backupServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import tcpConnection.TCPServer;

public class BackupServer implements Runnable {

	private TCPServer tcpServer;

	private int port;

	public BackupServer(int port) {
		this.port = port;

		try {
			this.tcpServer = new TCPServer(this.port);
			this.tcpServer.acceptSocket();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		new Thread(this).start();
	}

	@Override
	public void run() {
		System.out.println("[BACKUP SERVER] Loading");
		manager();
	}

	public void manager() {

		while (true) {
			tcpServer.receiveFile();
			System.out.println("[BACKUP SERVER] Received update");
			//restoreDatabase();
		}

	}

	public void restoreDatabase() {

		final List<String> command = new ArrayList<String>();
		
		command.add("C:\\Program Files\\PostgreSQL\\9.4\\bin\\pg_restore.exe"); 
		command.add("-h");
		command.add("localhost"); 
		command.add("-p");
		command.add("5432");
		command.add("-U");
		command.add("postgres");
		command.add("-d");
		command.add("sketchnary");
		command.add("-v");
		command.add("D:\\sketchnary.backup"); 
		
		ProcessBuilder pb = new ProcessBuilder(command);
		pb.environment().put("PGPASSWORD", "database123"); 
		
		try {
			
			final Process process = pb.start();
			final BufferedReader r = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			
			System.out.println("[BACKUP SERVER] Restoring database");
			
			String line = r.readLine();
			while (line != null) {
				System.err.println(line);
				line = r.readLine();
			}
			r.close();
			process.waitFor();
			process.destroy();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}

	}

}
