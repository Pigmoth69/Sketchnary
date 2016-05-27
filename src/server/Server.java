package server;

import java.io.IOException;

import backupServer.BackupServer;
import connection.Database;
import connection.DatabaseBackup;
import data.Online;
import data.ServerData;
import gameEngine.GameRoom;
import gameEngine.RoomsEngine;
import https.HttpsConnection;
import tcpConnection.TCPClient;
import tcpConnection.TCPServer;

public class Server {

	private Database database;
	private ServerData serverData;
	private BackupServer backupServer;
	private HttpsConnection connection;
	private RoomsEngine roomsEngine;
	private Online online;

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

		if (args.length != 4) {
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

		Server server = new Server(server_role, args[1], Integer.parseInt(args[2]));
		server.setupDatabaseConnection(args[3], "postgres", "database123");

		if (server.role) {
			//server.setupDatabaseBackup();
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

		TCPClient tcpClient = null;
		
		try {
			tcpClient = new TCPClient(hostname, port);
		} catch (Exception e) {
			e.printStackTrace();
		}

		DatabaseBackup db = new DatabaseBackup(tcpClient);
		db.start();

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
		GameRoom room3 = new GameRoom("sdkfj", "fwef", 5);
		GameRoom room4 = new GameRoom("ssretdkfj", "hh", 5);
		GameRoom room5 = new GameRoom("fg", "ty", 5);
		GameRoom room6 = new GameRoom("hfg", "ret", 5);
		GameRoom room7 = new GameRoom("vc", "gsd", 5);
		GameRoom room8 = new GameRoom("yu", "hdf", 5);
		GameRoom room9 = new GameRoom("fyu", "ry", 5);
		GameRoom room10 = new GameRoom("sy", "wer", 5);

		room.addPlayer(serverData.getPlayers().get(2));
		room.addPlayer(serverData.getPlayers().get(1));
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

		backupServer = new BackupServer(port);
		backupServer.start();

	}

}
