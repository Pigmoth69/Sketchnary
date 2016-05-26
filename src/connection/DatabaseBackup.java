package connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DatabaseBackup {

	public DatabaseBackup() {
	}

	public void restore() {

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
		comands.add("-b");
		comands.add("-v");
		comands.add("-f");
		comands.add("D:\\bkp.backup");
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

}
