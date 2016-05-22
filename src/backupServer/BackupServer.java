package backupServer;

import connection.Database;
import server.ServerData;
import tcpConnection.TCPServer;

public class BackupServer {
	
	private Database database;
	private ServerData serverData;

	private int port_c1;
	private int port_c2;
	
	public BackupServer(Database database, ServerData serverData, int port_c1, int port_c2){
		this.database = database;
		this.serverData = serverData;

		this.port_c1 = port_c1;
		this.port_c2 = port_c2;
	}
	
	public void manager(){
		
		try {
			TCPServer backup_server_c1 = new TCPServer(port_c1);
			TCPServer backup_server_c2 = new TCPServer(port_c2);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void updateDatabase(){
		
	}
	

}
