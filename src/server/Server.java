package server;

import backupServer.BackupServer;
import connection.Database;
import https.HttpsConnection;

public class Server {
	
	private Database database;
	private ServerData serverData;
	private BackupServer backupServer;
	
	private Boolean role;

	public Server(Boolean role) {
		serverData = new ServerData();
		
		this.role = role;
	}

	public static void main(String args[]) {
		
		if(args.length != 1){
			System.out.println("[ERROR] wrong number of arguments for server");
			System.exit(0);
		}
		
		Boolean server_role;
		
		if(args[0].equals("main"))
			server_role = true;
		else
			server_role = false;

		Server server = new Server(server_role);

		server.setupDatabaseConnection();
		
		if(server.role)
			server.setupHttpsConnection();
		else
			server.backupServer = new BackupServer(server.database, server.serverData, "localhost", 445);

	}

	/**
	 * Sets up the connection to the postgresql database
	 * Gets the data from the database
	 */
	public void setupDatabaseConnection() {

		database = new Database(serverData, "jdbc:postgresql://localhost:5432/sketchnary", "postgres",
				"database123");

		database.setup();
		database.recoverDatabase();

	}
	
	/**
	 * Sets up the https connection
	 */
	public void setupHttpsConnection(){
		
		HttpsConnection connection = new HttpsConnection(database);
		connection.setup();
		
	}

}
