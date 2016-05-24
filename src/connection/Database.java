package connection;

import server.Friend;
import server.Player;
import server.ServerData;

import java.sql.Statement;

import manager.Manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {

	private ServerData serverData;
	private DatabaseTracker databaseTracker;

	private String host;
	private String username;
	private String password;

	private Connection connection;

	public Database(ServerData serverData, String host, String username, String password) {
		this.databaseTracker = new DatabaseTracker();

		this.serverData = serverData;
		this.host = host;
		this.username = username;
		this.password = password;

		connection = null;
	}

	public void setup() {
		setConnection();
	}

	public void setupTracker(Manager manager) {
		databaseTracker.setManager(manager);
		databaseTracker.start();
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

			System.out.println("[DATABASE] Connection Failed! ");
			e.printStackTrace();
			return;

		}

		if (connection != null) {
			System.out.println("[DATABASE] Connection successful!");
		} else {
			System.out.println("[DATABASE] Failed to make connection!");
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
						player.getString("name"), player.getString("email"), player.getString("birthdate"),
						player.getString("country"), player.getInt("points"));
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

			statement.executeUpdate("INSERT INTO player (username, password, name, email, birthdate, country) " + "VALUES ('"
					+ player.getUsername() + "', '" + player.getPassword() + "', '" + player.getName() + "', '"
					+ player.getEmail() + "', '" + player.getBirthdate() + "', '" + player.getCountry() + "')");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertIntoDatabase(Friend friend) {

		Statement statement;

		try {
			statement = connection.createStatement();

			statement.executeUpdate("INSERT INTO friend (id_player, id_friend) " + "VALUES (" + friend.getIdPlayer()
					+ ", " + friend.getIdFriend() + ");");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void selectFromDatabase() {

		Statement statement;

		try {

			statement = connection.createStatement();

			ResultSet result = statement.executeQuery("SELECT name FROM player;");

			while (result.next()) {
				System.out.println(result.getString("name"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void deleteFromDatabase(int id_player) {

		Statement statement;

		try {

			statement = connection.createStatement();

			statement.executeUpdate("DELETE FROM Player WHERE id = " + id_player);

			System.out.println("[DATABASE] Deleted player successfully!");

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void deleteFromDatabase(int id_player, int id_friend) {

		Statement statement;

		try {

			statement = connection.createStatement();

			statement.executeUpdate(
					"DELETE FROM Friend WHERE id_player = " + id_player + " AND id_friend = " + id_friend + ";");

			System.out.println("[DATABASE] Deleted friend sucessfully!");

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void addFriend(int id_player, int id_friend) {

		Statement statement;

		try {

			statement = connection.createStatement();

			statement.executeUpdate(
					"INSERT INTO Friend (id_player, id_friend) VALUES (" + id_player + "," + id_friend + ");");

			System.out.println("[DATABASE] Added friend successfully!");

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void addPlayer(Player player) {

		Statement statement;
		String query;

		query = "INSERT INTO Player (id, username, password, name, email, birthdate, country) VALUES (" + player.getId()
				+ ",'" + player.getUsername() + "','" + player.getPassword() + "','" + player.getName() + "','"
				+ player.getEmail() + "'," + player.getBirthdate() + ",'" + player.getCountry() + "');";

		try {

			statement = connection.createStatement();

			statement.executeUpdate(query);

			databaseTracker.addQuery(query);

			System.out.println("[DATABASE] Added player successfully!");

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Verifies the player data - username and password
	 * 
	 * @param username
	 * @param password
	 * @return boolean
	 */
	public String playerVerification(String email, String password) {

		Statement email_statement;
		Statement password_statement;

		try {

			email_statement = connection.createStatement();

			ResultSet email_result = email_statement
					.executeQuery("SELECT email FROM player WHERE email = '" + email + "';");

			if (!email_result.next()) {
				System.out.println("[DATABASE] No players registered with that email!");
				return "email";
			} else {

				password_statement = connection.createStatement();

				ResultSet password_result = password_statement
						.executeQuery("SELECT password FROM player WHERE password = '" + password + "';");

				if (!password_result.next()) {
					System.out.println("[DATABASE] Wrong password!");
					return "password";
				} else
					return "true";

			}

		} catch (SQLException e) {
			e.printStackTrace();
			return "exception";
		}

	}

	public Boolean editPlayerPassword(String email, String new_password) {

		Statement edit;
		String query;

		query = "UPDATE player SET password='" + new_password + "' WHERE email='" + email + "';";

		try {

			edit = connection.createStatement();

			edit.executeUpdate(query);

			databaseTracker.addQuery(query);

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	public Boolean editPlayerName(String email, String new_name) {

		Statement edit;
		String query;

		query = "UPDATE player SET name='" + new_name + "' WHERE email='" + email + "';";

		try {

			edit = connection.createStatement();

			edit.executeUpdate(query);

			databaseTracker.addQuery(query);

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	public Boolean editPlayerUsername(String email, String new_username) {

		Statement edit;
		String query;

		query = "UPDATE player SET username='" + new_username + "' WHERE email='" + email + "';";

		try {

			edit = connection.createStatement();

			edit.executeUpdate(query);

			databaseTracker.addQuery(query);

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	public Boolean editPlayerBirthdate(String email, String new_birthdate) {

		Statement edit;
		String query;

		query = "UPDATE player SET birthdate = '" + new_birthdate + "' WHERE email='" + email + "';";

		try {

			edit = connection.createStatement();

			edit.executeUpdate(query);

			databaseTracker.addQuery(query);

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	public Boolean editPlayerCountry(String email, String new_country) {

		Statement edit;
		String query;

		query = "UPDATE player SET country='" + new_country + "' WHERE email='" + email + "';";

		try {

			edit = connection.createStatement();

			edit.executeUpdate(query);

			databaseTracker.addQuery(query);

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	public int getPlayerPoints(String email) {

		Statement player_points;

		try {

			player_points = connection.createStatement();

			ResultSet points_result;

			points_result = player_points.executeQuery("SELECT points FROM player WHERE email = '" + email + "';");

			if (!points_result.next()) {
				System.out.println("[DATABASE] No players registered with that email!");
				return 0;
			} else {
				return points_result.getInt("points");
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		return 0;

	}

	public Boolean createPlayer(String username, String password, String name, String email, String birthdate,
			String country) {

		Statement edit;
		String query;

		query = "INSERT INTO Player (username, password, name, email, age, country) VALUES ('" + username + "','"
				+ password + "','" + name + "','" + email + "','" + birthdate + "','" + country + "');";

		try {

			edit = connection.createStatement();

			edit.executeUpdate(query);

			databaseTracker.addQuery(query);

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	public Boolean deletePlayer(String email) {

		Statement delete;
		String query;

		query = "DELETE FROM player WHERE email = '" + email + "';";

		try {

			delete = connection.createStatement();

			delete.executeUpdate(query);

			databaseTracker.addQuery(query);

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	public Boolean updateDatabase(String query) {

		Statement s;

		try {

			s = connection.createStatement();

			s.executeUpdate(query);

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

}
