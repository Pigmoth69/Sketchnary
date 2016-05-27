package connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import tcpConnection.TCPClient;

public class DatabaseBackup implements Runnable {

	private TCPClient tcp;

	private String filename = "D:\\sketchnary.backup";

	public DatabaseBackup(TCPClient tcp) {
		this.tcp = tcp;
	}
	
	public void start(){
		new Thread(this).start();
	}

	public void backup() {

		final List<String> comands = new ArrayList<String>();

		comands.add("C:\\Program Files\\PostgreSQL\\9.4\\bin\\pg_dump.exe");
		comands.add("-h");
		comands.add("localhost");
		comands.add("-p");
		comands.add("5432");
		comands.add("-U");
		comands.add("postgres");
		comands.add("-F");
		comands.add("c");
		comands.add("-a");
		comands.add("-v");
		comands.add("-f");
		comands.add("D:\\sketchnary.backup");
		comands.add("sketchnary");

		ProcessBuilder pb = new ProcessBuilder(comands);
		pb.environment().put("PGPASSWORD", "database123");

		try {

			final Process process = pb.start();
			final BufferedReader r = new BufferedReader(new InputStreamReader(process.getErrorStream()));
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

	public void sendBackup() {
		System.out.println("[DATABASE BACKUP] Sending backup");
		tcp.sendFile(filename);
	}

	@Override
	public void run() {

		while (true) {
			
			backup();
			sendBackup();
			
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

}
