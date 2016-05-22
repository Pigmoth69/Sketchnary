package server;

import java.io.IOException;
import java.util.ArrayList;

import connection.Database;
import tcpConnection.TCPClient;

public class Manager {
	
	private Database database;
	private Server server;
	private ServerData serverData;
	private TCPClient tcpClient;
	
	private String hostname;
	private int port;
	private ArrayList<String> send_queue;
	
	public Manager(Database database, Server server, ServerData serverData, String hostname, int port){
		this.database = database;
		this.server = server;
		
		this.hostname = hostname;
		this.port = port;
		this.send_queue = new ArrayList<String>();
	}
	
	public Boolean updateBackupServer(ArrayList<String> queries){
		
		try {
			tcpClient = new TCPClient(hostname, port);
			
			sendQueries(queries);
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return null;
		
	}
	
	public int sendQueries(ArrayList<String> queries){
		
		for(int i = 0; i < queries.size(); i++){
			
			try {
				
				tcpClient.send(queries.get(i));
				
				verifyIntegrity(queries.get(i));
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		return -1;
		
	}

	/**
	 * Verify whether the correct query was received
	 * @param string
	 * @return
	 */
	private Boolean verifyIntegrity(String string) {
		
		String received = null;
		
		try {
			received = tcpClient.receive();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(received.equals(string))
			return true;
		
		return false;
		
	}
	
	

}
