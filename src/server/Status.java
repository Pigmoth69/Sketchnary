package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.rmi.ConnectException;

import tcpConnection.TCPClient;
import utilities.Constants;

public class Status implements Runnable {

	private TCPClient tcp;

	private int port;
	private String status;
	private String hostname;
	private int tries;

	public Status(String hostname, int port) {
		this.port = port;
		this.status = Constants.OK;
		this.tries = 0;
	}
	
	public Boolean connect(int port){
		
		try {
			this.tcp = new TCPClient(hostname, port);
		} catch (ConnectException e) {
			e.printStackTrace();
			return false;
		} catch (UnknownHostException e2){
			e2.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
		
	}

	@Override
	public void run() {

		while (true) {

			try {

				tcp.send(status);

				String received = tcp.receive();

				while (received == null && tries < 3) {
					tcp.send(status);
					tries++;
					received = tcp.receive();
				}
				if (received == null && tries >= 3) {
					tries = 0;
					System.out.println("[STATUS] Backup Server is dead");
					startAnotherServer();

					try {
						Thread.sleep(400);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void startAnotherServer() {

		Process p = null;
		BufferedReader inStream = null;

		System.out.println("[STATUS] Starting another backup server");

		try {
			p = Runtime.getRuntime().exec("cmd.exe /c cd \"\" & start cmd.exe /k \"java -classpath C:\\Users\\utilizador\\git\\Sketchnary\\bin server.Server backup backup_sketchnary localhost 1435 restore");
			//theProcess = Runtime.getRuntime().exec("java Server backup backup_sketchnary localhost 1435");
			//theProcess = Runtime.getRuntime().exec("java -classpath C:\\Users\\utilizador\\git\\Sketchnary\\bin server.Server backup backup_sketchnary localhost 1435");
		} catch (IOException e) {
			System.err.println("Error on exec() method");
			e.printStackTrace();
		}

		try {
			inStream = new BufferedReader(new InputStreamReader(p.getInputStream()));
			System.out.println(inStream.readLine());
		} catch (IOException e) {
			System.err.println("Error on inStream.readLine()");
			e.printStackTrace();
		}

		System.out.println("[STATUS] Started another server");

	}

	public void start() {
		new Thread(this).start();

	}

}
