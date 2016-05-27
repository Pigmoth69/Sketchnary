package server;

import backupServer.BackupServer;
import connection.Database;
import connection.DatabaseBackup;
import data.Online;
import data.ServerData;
import gameEngine.GameRoom;
import gameEngine.RoomsEngine;
import https.HttpsConnection;

public class Server {

	private Database database;
	private ServerData serverData;
	private BackupServer backupServer;
	private HttpsConnection connection;
	private RoomsEngine roomsEngine;
	private Online online;

	private Boolean role;

	public Server(Boolean role) {
		serverData = new ServerData();

		this.role = role;
	}

	public static void main(String args[]) {

		if (args.length != 2) {
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

		Server server = new Server(server_role);
		server.setupDatabaseConnection(args[1], "postgres", "database123");

		if (server.role) {
			server.setupDatabaseBackup();
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
		database.recoverDatabase();

	}

	/**
	 * Sets up the database backup
	 */
	public void setupDatabaseBackup() {

		DatabaseBackup db = new DatabaseBackup();
		db.start();

	}

	/**
	 * Sets up the https connection
	 */
	public void setupHttpsConnection() {

		connection = new HttpsConnection(database, roomsEngine, online, serverData);
		connection.setup();

	}

	/**
	 * Sets up the rooms engine
	 */
	public void setupRooms() {

		roomsEngine = new RoomsEngine();

		GameRoom room = new GameRoom("CoolRoom", "room 1");
		GameRoom room2 = new GameRoom("Thrust", "room 2");
		GameRoom room3 = new GameRoom("sdkfj", "fwef");
		GameRoom room4 = new GameRoom("ssretdkfj", "hh");
		GameRoom room5 = new GameRoom("fg", "ty");
		GameRoom room6 = new GameRoom("hfg", "ret");
		GameRoom room7 = new GameRoom("vc", "gsd");
		GameRoom room8 = new GameRoom("yu", "hdf");
		GameRoom room9 = new GameRoom("fyu", "ry");
		GameRoom room10 = new GameRoom("sy", "wer");

		room2.addPlayer(serverData.getPlayers().get(0));
		room2.addPlayer(serverData.getPlayers().get(4));
		
		
		room3.addPlayer(serverData.getPlayers().get(2));
		room4.addPlayer(serverData.getPlayers().get(1));
		room5.addPlayer(serverData.getPlayers().get(0));
		room5.addPlayer(serverData.getPlayers().get(4));
		
		room6.addPlayer(serverData.getPlayers().get(2));
		room7.addPlayer(serverData.getPlayers().get(1));
		room8.addPlayer(serverData.getPlayers().get(0));
		room9.addPlayer(serverData.getPlayers().get(4));
		
		room6.addPlayer(serverData.getPlayers().get(5));
		room7.addPlayer(serverData.getPlayers().get(6));
		room8.addPlayer(serverData.getPlayers().get(2));
		room9.addPlayer(serverData.getPlayers().get(3));
		
		room10.addPlayer(serverData.getPlayers().get(2));
		room10.addPlayer(serverData.getPlayers().get(1));
		room10.addPlayer(serverData.getPlayers().get(0));
		room10.addPlayer(serverData.getPlayers().get(4));

		roomsEngine.addRoom(room);
		roomsEngine.addRoom(room2);
		roomsEngine.addRoom(room3);
		roomsEngine.addRoom(room4);
		roomsEngine.addRoom(room5);
		roomsEngine.addRoom(room6);
		roomsEngine.addRoom(room7);
		roomsEngine.addRoom(room8);
		roomsEngine.addRoom(room9);
		roomsEngine.addRoom(room10);
		
		online = new Online();

	}

	/**
	 * Sets up the backup server
	 */
	public void setupBackup() {

		backupServer = new BackupServer();
		backupServer.start();

	}

}
