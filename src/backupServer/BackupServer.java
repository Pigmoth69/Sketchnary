package backupServer;

import connection.Database;
import server.ServerData;
import tcpConnection.TCPServer;

public class BackupServer {
	
	private Database database;
	private ServerData serverData;

	private int port;
	
	public BackupServer(Database database, ServerData serverData, int port){
		this.database = database;
		this.serverData = serverData;

		this.port = port;
	}
	
	public void manager(){
		
		try {
			TCPServer backup_server = new TCPServer(port);
			
			backup_server.receive();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void updateDatabase(){
		
	}
	

}
