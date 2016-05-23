package server;

import backupServer.BackupServer;
import connection.Database;
import https.HttpsConnection;
import manager.Manager;

public class Server {

	private Database database;
	private ServerData serverData;
	private BackupServer backupServer;
	private HttpsConnection connection;
	private Manager manager;

	private Boolean role;
	private String hostname;
	private int port_c1;
	private int port_c2;

	public Server(Boolean role, String hostname, int port_c1, int port_c2) {
		serverData = new ServerData();

		this.role = role;
		this.hostname = hostname;
		this.port_c1 = port_c1;
		this.port_c2 = port_c2;
	}

	public static void main(String args[]) {

		if (args.length != 4) {
			System.out.println("[ERROR] wrong number of arguments for server");
			System.exit(0);
		}

		Boolean server_role;


		if (args[0].equals("main"))
			server_role = true;
		else
			server_role = false;

		Server server = new Server(server_role, args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));

		server.setupDatabaseConnection();


		if (server.role) {
			//server.manager = new Manager(server.database, server, server.serverData, server.hostname, server.port_c1,
					//server.port_c2);

			server.setupHttpsConnection();
		} else {
			server.backupServer = new BackupServer(server.database, server.serverData, server.port_c1, server.port_c2);

			server.backupServer.manager();
		}

	}

	/**
	 * Sets up the connection to the postgresql database Gets the data from the
	 * database
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
