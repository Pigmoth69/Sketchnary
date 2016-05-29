package backupServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import server.Server;
import tcpConnection.TCPServer;
import utilities.Constants;

public class BackupStatus implements Runnable {

	private TCPServer tcp;
	private Server server;

	private int port;
	private int tries;
	private String hostname;

	public BackupStatus(Server server, int port, String hostname) {
		this.server = server;

		this.port = port;
		this.tries = 0;
		this.hostname = hostname;

	}
	
	public void setupTcp(){
		try {
			tcp = new TCPServer(this.port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		new Thread(this).start();
	}

	@Override
	public void run() {

		while (true) {

			String received = null;

			try {
				received = tcp.receive();
			} catch (IOException e) {
				e.printStackTrace();
			}

			while ((received == null || received != Constants.OK) && tries < 3) {

				try {
					received = tcp.receive();
				} catch (IOException e) {
					e.printStackTrace();
					tries = 3;
					received = null;
					break;
				}

			}
			if ((received == null || received != Constants.OK) && tries >= 3) {
				System.out.println("[BACKUP STATUS] Server is dead");
				restoreDatabase();
				assumeFunctions();
				break;
			}

			try {
				tcp.send(Constants.OK);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	private void assumeFunctions() {

		startAnotherBackup();

		server.setupDatabaseBackup();
		server.setupRooms();
		server.setupHttpsConnection();
		server.setupStatus(hostname, port);

	}

	public void startAnotherBackup() {

		Process theProcess = null;
		BufferedReader inStream = null;

		System.out.println("[STATUS] Starting another backup server");

		try {
			theProcess = Runtime.getRuntime().exec("java Sketchnary backup backup_sketchnary localhost 1936");
		} catch (IOException e) {
			System.err.println("Error on exec() method");
			e.printStackTrace();
		}

		try {
			inStream = new BufferedReader(new InputStreamReader(theProcess.getInputStream()));
			System.out.println(inStream.readLine());
		} catch (IOException e) {
			System.err.println("Error on inStream.readLine()");
			e.printStackTrace();
		}

		System.out.println("[STATUS] Started another server");

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
		command.add("backup_sketchnary");
		command.add("-v");
		command.add("D:\\sketchnary.backup");

		ProcessBuilder pb = new ProcessBuilder(command);
		pb.environment().put("PGPASSWORD", "database123");

		try {

			final Process process = pb.start();
			final BufferedReader r = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			String line = r.readLine();
			while (line != null) {
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
