package server;

import backupServer.BackupServer;
import connection.Database;
import connection.DatabaseBackup;
import data.Online;
import data.ServerData;
import gameEngine.GameRoom;
import gameEngine.RoomsEngine;
import https.HttpsConnection;
import manager.Manager;

public class Server {

	private Database database;
	private ServerData serverData;
	private BackupServer backupServer;
	private HttpsConnection connection;
	private Manager manager;
	private RoomsEngine roomsEngine;
	private Online online;

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
			DatabaseBackup db = new DatabaseBackup();
			//db.restore();
			server.setupManager();
			server.setupRooms();
			server.setupHttpsConnection();
		} else {
			server.setupBackup();
		}

	}

	/**
	 * Sets up the connection to the postgresql database Gets the data from the
	 * database
	 */
	public void setupDatabaseConnection(String database_name, String owner, String password) {
		database = new Database(serverData, "jdbc:postgresql://localhost:5432/" + database_name, owner, password);

		database.setup();

	}
	
	/**
	 * Sets up the manager and the database tracker
	 */
	public void setupManager(){
		
		manager = new Manager(hostname, port_c1, port_c2);
		manager.start();

		database.setupTracker(manager);
		
		database.recoverDatabase();
		
	}

	/**
	 * Sets up the https connection
	 */
	public void setupHttpsConnection() {

		connection = new HttpsConnection(database, roomsEngine, online);
		connection.setup();

	}

	/**
	 * Sets up the rooms engine 
	 */
	public void setupRooms() {
		
		roomsEngine = new RoomsEngine();

		GameRoom room = new GameRoom("CoolRoom", "room 1", 3);
		GameRoom room2 = new GameRoom("Thrust", "room 2", 4);
		
		room.addPlayer(serverData.getPlayers().get(2));
		room.addPlayer(serverData.getPlayers().get(1));
		room2.addPlayer(serverData.getPlayers().get(0));
		room2.addPlayer(serverData.getPlayers().get(4));
		
		roomsEngine.addRoom(room);
		roomsEngine.addRoom(room2);
		
		online = new Online();
		
	}
	
	/**
	 * Sets up the backup server
	 */
	public void setupBackup(){
		
		backupServer = new BackupServer(database, port_c1, port_c2);
		backupServer.start();
		
	}

}
