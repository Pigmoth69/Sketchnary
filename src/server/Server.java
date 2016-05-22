package server;

import backupServer.BackupServer;
import connection.Database;
import https.HttpsConnection;

public class Server {
	
	private Database database;
	private ServerData serverData;
	private BackupServer backupServer;
	private HttpsConnection connection;
	private Manager manager;
	
	private Boolean role;
	private String hostname;
	private int port;

	public Server(Boolean role, String hostname, int port) {
		serverData = new ServerData();
		
		this.role = role;
		this.hostname = hostname;
		this.port = port;
	}

	public static void main(String args[]) {
		
		if(args.length != 3){
			System.out.println("[ERROR] wrong number of arguments for server");
			System.exit(0);
		}
		
		Boolean server_role;
		
		if(args[0].equals("main"))
			server_role = true;
		else
			server_role = false;

		Server server = new Server(server_role, args[1], Integer.parseInt(args[2]));

		server.setupDatabaseConnection();
		
		if(server.role){
			server.manager = new Manager(server.database, server, server.serverData, server.hostname, server.port);
			
			server.setupHttpsConnection();
		}
		else
			server.backupServer = new BackupServer(server.database, server.serverData, server.port);

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
		
		connection = new HttpsConnection(database);
		connection.setup();
		
	}

}
