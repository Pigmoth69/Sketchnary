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

		if (args.length != 5) {
			System.out.println("[SERVER] [ERROR] wrong number of arguments for server");
			System.exit(0);
		}

		Boolean server_role = true;


		if (args[0].equals("main"))
			server_role = true;
		else if (args[0].equals("backup"))
			server_role = false;
		else
			System.exit(1);

		Server server = new Server(server_role, args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
		server.setupDatabaseConnection(args[4], "postgres", "database123");

		if (server.role) {

			server.manager = new Manager(server.hostname, server.port_c1, server.port_c2);
			server.manager.start();
			
			server.database.setupTracker(server.manager);
			
			server.setupHttpsConnection();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			Player player = new Player(3, "coolGuy", "3cool3", "francis", "francis@gmail.com", 18, "France");
			server.database.addPlayer(player);
			
		} else {
			server.backupServer = new BackupServer(server.database, server.port_c1, server.port_c2);
			server.backupServer.start();
		}

	}

	/**
	 * Sets up the connection to the postgresql database 
	 * Gets the data from the database
	 */
	public void setupDatabaseConnection(String database_name, String owner, String password) {
		database = new Database(serverData, "jdbc:postgresql://localhost:5432/" + database_name, owner, password);

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
