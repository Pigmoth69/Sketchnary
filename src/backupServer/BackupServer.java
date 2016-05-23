package backupServer;

import connection.Database;
import server.ServerData;
import tcpConnection.Channel;
import tcpConnection.TCPServer;
import utilities.Constants;

public class BackupServer {

	private Database database;
	private ServerData serverData;
	private Channel channel;

	private int port_c1;
	private int port_c2;

	public BackupServer(Database database, ServerData serverData, int port_c1,
			int port_c2) {
		this.database = database;
		this.serverData = serverData;

		this.port_c1 = port_c1;
		this.port_c2 = port_c2;
	}

	public void manager() {

		try {
			channel = new Channel(null, port_c1, port_c2);
			channel.createChannels(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String updateDatabase(String query) {
		if (database.updateDatabase(query))
			return Constants.BACKUP_DB;
		else
			return Constants.ERROR_BK_DB;
	}

	public String serverExchange(String query) {
		if (channel.exchangeC1(true, query).equals(Constants.OK)) {
			if (channel.exchangeC2(true, query).equals(Constants.OK)) {
				return Constants.OK;
			} else {
				return Constants.ERROR2;
			}
		}
		else {
			return Constants.ERROR1;
		}
	}
	
	public void serverConnection(String query){
		int tries = 3;
		
		if(serverExchange(query).equals(Constants.OK))
				return;
		else{
			while(tries > 0){
				serverExchange(query);
				tries--;
			}
		}
	}
}
