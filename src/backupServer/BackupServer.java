package backupServer;

import connection.Database;
import server.ServerData;
import tcpConnection.TCPClient;

public class BackupServer {
	
	private Database database;
	private ServerData serverData;
	
	private String hostname;
	private int port;
	
	public BackupServer(Database database, ServerData serverData, String hostname, int port){
		this.database = database;
		this.serverData = serverData;
		
		this.hostname = hostname;
		this.port = port;
	}
	
	public void manager(){
		
		try {
			TCPClient backup_client = new TCPClient(hostname, port);
			
			backup_client.receive();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void updateDatabase(){
		
	}
	

}
