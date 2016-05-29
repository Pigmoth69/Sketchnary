package backupServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.rmi.ConnectException;
import java.util.ArrayList;
import java.util.List;

import server.Server;
import tcpConnection.TCPServer;

public class BackupStatus implements Runnable {

	private TCPServer tcp;
	private Server server;

	private int port;
	private int tries;
	private String hostname;
	private int connect_tries;

	public BackupStatus(Server server, int port, String hostname) {
		this.server = server;

		this.port = port;
		this.tries = 0;
		this.hostname = hostname;
		this.connect_tries = 0;
	}

	public Boolean connect() {

		Boolean success = false;
		Boolean exception = false;

		while (!success) {
			try {
				this.tcp = new TCPServer(port);
			} catch (ConnectException e) {
				e.printStackTrace();
				System.out.println("connect");
				exception = true;
			} catch (UnknownHostException e2) {
				e2.printStackTrace();
				System.out.println("unknown");
				exception = true;
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("io");
				exception = true;
			}

			if (!exception){
				this.tcp.acceptSocket();
				System.out.println("Accept Socket");
				success = true;
			}

			exception = false;
			connect_tries++;

			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (connect_tries > 2) {
				System.out.println("I TRIED SO HARD AND GOT SO FAR");
				break;
			}

		}

		if (connect_tries >= 3 && !success)
			return false;
		return true;
	}

	public void start() {
		new Thread(this).start();
	}

	@Override
	public void run() {

		Boolean enter = false;

		if (!connect())
			assumeFunctions();
		else
			enter = true;

		if (enter) {

			System.out.println("IM HERE");
			
			while (true) {

				if (tries >= 10) {
					System.out.println("TRIES MAIOR~");
					restoreDatabase();
					System.out.println("RESTORED DATABASE");
					assumeFunctions();
					System.out.println("ASSUMING FUNCTIONS");
					break;
				}

				String received = null;

				try {
					received = tcp.receive();
				} catch (ConnectException e) {
					e.printStackTrace();
					tries++;
				} catch (UnknownHostException e) {
					e.printStackTrace();
					tries++;
				} catch (IOException e) {
					e.printStackTrace();
					tries++;
				} catch (NullPointerException e) {
					e.printStackTrace();
					tries++;
				}

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}

	}

	private void assumeFunctions() {

		startAnotherBackup();

		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

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
			theProcess = Runtime.getRuntime().exec("java Sketchnary backup backup_sketchnary localhost 1234");
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
