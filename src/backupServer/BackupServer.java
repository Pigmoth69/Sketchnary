package backupServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BackupServer implements Runnable {

	public BackupServer() {}

	public void start() {
		new Thread(this).start();
	}

	@Override
	public void run() {
		System.out.println("[BACKUP SERVER] Loading");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		manager();
	}

	public void manager() {
		
		System.out.println("[BACKUP SERVER] Restoring database");
		//restoreDatabase();
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
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
