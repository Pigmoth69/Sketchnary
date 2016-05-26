package server;

import backupServer.BackupServer;
import connection.Database;
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
			
			server.setupRooms();

			server.manager = new Manager(server.hostname, server.port_c1, server.port_c2);
			server.manager.start();

			server.database.setupTracker(server.manager);

			server.setupHttpsConnection();

		} else {
			server.backupServer = new BackupServer(server.database, server.port_c1, server.port_c2);
			server.backupServer.start();
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
	 * Sets up the https connection
	 */
	public void setupHttpsConnection() {

		connection = new HttpsConnection(database, roomsEngine);
		connection.setup();

	}

	public void setupRooms() {
		
		roomsEngine = new RoomsEngine();

		Player player = new Player(1, "user", "fds", "sdf", "pintou@gmail.com", "2016-12-12", "Portugal", 20);
		Player player2 = new Player(2, "user2", "t", "sdretf", "pintou2@gmail.com", "2016-08-12", "Portugal", 50);
		Player player3 = new Player(3, "user3", "tr", "5t", "pintou3@gmail.com", "2016-04-10", "Portugal", 30);
		Player player4 = new Player(4, "user4", "er", "54", "pintou4@gmail.com", "2016-11-18", "Portugal", 40);

		GameRoom room = new GameRoom("room 1", "room 1", 3);
		GameRoom room2 = new GameRoom("room 2", "room 2", 4);
		
		room.addPlayer(player);
		room.addPlayer(player4);
		room2.addPlayer(player2);
		room2.addPlayer(player3);
		
		roomsEngine.addRoom(room);
		roomsEngine.addRoom(room2);
		
	}

}
