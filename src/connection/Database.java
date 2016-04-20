package connection;

import server.Friend;
import server.Player;
import server.Server;
import server.ServerData;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {

	private Server server;
	private ServerData serverData;
	private String host;
	private String username;
	private String password;

	private Connection connection;

	public Database(Server server, ServerData serverData, String host, String username, String password) {
		this.server = server;
		this.serverData = serverData;

		this.host = host;
		this.username = username;
		this.password = password;

		connection = null;
	}

	public void setup() {
		setConnection();
	}

	private void setConnection() {

		System.out.println("-------- PostgreSQL " + "JDBC Connection Testing ------------");

		try {

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;

		}

		System.out.println("PostgreSQL JDBC Driver Registered!");

		try {

			connection = DriverManager.getConnection(host, username, password);

		} catch (SQLException e) {

			System.out.println("Connection Failed! ");
			e.printStackTrace();
			return;

		}

		if (connection != null) {
			System.out.println("Connection successful!");
		} else {
			System.out.println("Failed to make connection!");
		}

	}

	/**
	 * Get all the data from the database
	 */
	public void recoverDatabase() {

		Statement recover;

		try {
			recover = connection.createStatement();

			ResultSet player = recover.executeQuery("SELECT * FROM player");

			while (player.next()) {

				Player pl = new Player(player.getInt("id"), player.getString("username"), player.getString("password"),
						player.getString("name"), player.getString("email"), player.getInt("age"),
						player.getString("country"));
				serverData.addPlayer(pl);

			}

			ResultSet friend = recover.executeQuery("SELECT * FROM friend");

			while (friend.next()) {

				Friend fr = new Friend(friend.getInt("id_player"), friend.getInt("id_friend"));
				serverData.addFriend(fr);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		serverData.updateFriendsList();

	}

	public void insertIntoDatabase(Player player) {

		Statement statement;

		try {
			statement = connection.createStatement();

			statement.executeUpdate("INSERT INTO player (username, password, name, email, age, country) " + "VALUES ('"
					+ player.getUsername() + "', '" + player.getPassword() + "', '" + player.getName() + "', '"
					+ player.getEmail() + "', " + player.getAge() + ", '" + player.getCountry() + "')");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertIntoDatabase(Friend friend) {

		Statement statement;

		try {
			statement = connection.createStatement();

			statement.executeUpdate("INSERT INTO friend (id_player, id_friend) " + "VALUES ("
					+ friend.getIdPlayer() + ", " + friend.getIdFriend() + ")");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void selectFromDatabase() {

		Statement statement;

		try {

			statement = connection.createStatement();

			ResultSet result = statement.executeQuery("SELECT name FROM player");

			while (result.next()) {
				System.out.println(result.getString("name"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
