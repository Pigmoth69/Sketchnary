package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import backupServer.BackupStatus;
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
	private BackupStatus backupStatus;
	private HttpsConnection connection;
	private RoomsEngine roomsEngine;
	private Online online;
	private Status status;

	private Boolean role;

	public Server(Boolean role) {
		serverData = new ServerData();

		this.role = role;
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

		Server server = new Server(server_role);

		if (args[4].equals("restore"))
			server.restoreDatabase();
		else
			server.setupDatabaseConnection(args[1], "postgres", "database123");

		if (server.role) {
			server.setupDatabaseBackup();
			server.setupRooms();
			server.setupHttpsConnection();
			//server.setupStatus(args[2], Integer.parseInt(args[3]));
		} else {
			server.setupBackup(args[2], Integer.parseInt(args[3]));
		}

	}

	public void setupStatus(String hostname, int port) {
		
		status = new Status(hostname, port);
		if(!status.connect(port)){
			status.startAnotherServer();
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		while(!status.connect(1937)){}
		status.start();
		System.out.println("Started");
		
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

		GameRoom room = new GameRoom("CoolRoom", "room 1", 1735);
		GameRoom room2 = new GameRoom("Thrust", "room 2", 1761);
		GameRoom room3 = new GameRoom("sdkfj", "fwef", 1776);
		GameRoom room4 = new GameRoom("ssretdkfj", "hh", 1792);
		GameRoom room5 = new GameRoom("fg", "ty", 1801);
		GameRoom room6 = new GameRoom("hfg", "ret", 1812);
		GameRoom room7 = new GameRoom("vc", "gsd", 1813);
		GameRoom room8 = new GameRoom("yu", "hdf", 1863);
		GameRoom room9 = new GameRoom("fyu", "ry", 1886);
		GameRoom room10 = new GameRoom("sy", "wer", 1920);

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
	public void setupBackup(String hostname, int port) {

		backupStatus = new BackupStatus(this, port, hostname);
		backupStatus.setupTcp();
		backupStatus.start();

	}

	public void restoreDatabase() {

		final List<String> command = new ArrayList<String>();

		command.add("C:\\Program Files\\PostgreSQL\\9.4\\bin\\pg_restore.exe");
		command.add("-h");
		command.add("localhost");
		command.add("-p");
		command.add("5432");
		command.add("-U");
		command.add("postgres");
		command.add("-d");
		command.add("backup_sketchnary");
		command.add("-v");
		command.add("D:\\sketchnary.backup");

		ProcessBuilder pb = new ProcessBuilder(command);
		pb.environment().put("PGPASSWORD", "database123");

		try {

			final Process process = pb.start();
			final BufferedReader r = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			String line = r.readLine();
			while (line != null) {
				System.err.println(line);
				line = r.readLine();
			}
			r.close();
			process.waitFor();
			process.destroy();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}

	}

}
