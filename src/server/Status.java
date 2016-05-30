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
	private int connect_tries;
	private Boolean connect;

	public Status(String hostname, int port) {
		this.port = port;
		this.status = Constants.OK;
		this.tries = 0;
		this.connect_tries = 0;
	}

	public Boolean connect() {

		Boolean success = false;
		Boolean exception = false;

		while (!success) {
			try {
				this.tcp = new TCPClient(hostname, port);
			} catch (ConnectException e) {
				e.printStackTrace();
				System.out.println("connect");
				exception = true;
				connect_tries++;
			} catch (UnknownHostException e2) {
				e2.printStackTrace();
				System.out.println("unknown");
				exception = true;
				connect_tries++;
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("io");
				exception = true;
				connect_tries++;
			}
			
			System.out.println(connect_tries);
			
			if(!exception)
				success = true;
			
			exception = false;
			
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(connect_tries > 2){
				System.out.println("NO MORE TRIES");
				break;
			}
			
		}

		if(connect_tries >= 3 && !success)
			return false;
		return true;

	}

	@Override
	public void run() {
		
		connect = connect();
		
		if(!connect)
			startAnotherServer();
		
		System.out.println("CONNECT " + connect);

		while (true) {
			
			try {
				tcp.send(status);
			} catch (ConnectException e) {
				e.printStackTrace();
				System.out.println("CONNECT EXCEPTION INSIDE");
				tries++;
			} catch (UnknownHostException e) {
				e.printStackTrace();
				System.out.println("UNKNOWN EXCEPTION INSIDE");
				tries++;
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("IO EXCEPTION INSIDE");
				tries++;
			} catch (NullPointerException e) {
				e.printStackTrace();
				System.out.println("NULL EXCEPTION INSIDE");
				tries++;
			}
			
			if (tries >= 3) {
				startAnotherServer();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				tries = 0;
			}
			
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}

	}

	public void startAnotherServer() {

		Process p = null;
		BufferedReader inStream = null;

		System.out.println("[STATUS] Starting another backup server");

		try {
			p = Runtime.getRuntime().exec(
					"cmd.exe /c cd \"\" & start cmd.exe /k \"java -classpath C:\\Users\\utilizador\\git\\Sketchnary\\bin server.Server backup backup_sketchnary 127.0.0.1 1234 restore");
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

		connect_tries = 0;
		connect = connect();
		
	}

	public void start() {
		new Thread(this).start();

	}

}
