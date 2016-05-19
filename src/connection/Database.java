package connection;

import server.Friend;
import server.Player;
import server.ServerData;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {

	private ServerData serverData;
	private String host;
	private String username;
	private String password;

	private Connection connection;

	public Database(ServerData serverData, String host, String username, String password) {
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

			System.out.println("Deleted player successfully!");

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

			System.out.println("Deleted friend sucessfully!");

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

			System.out.println("Added friend successfully!");

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void addPlayer(Player player) {

		Statement statement;

		try {

			statement = connection.createStatement();

			statement.executeUpdate(
					"INSERT INTO Player (id, username, password, name, email, age, country) VALUES (" + player.getId()
							+ ",'" + player.getUsername() + "','" + player.getPassword() + "','" + player.getName()
							+ "','" + player.getEmail() + "'," + player.getAge() + ",'" + player.getCountry() + "');");

			System.out.println("Added player successfully!");

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
	public Boolean playerVerification(String username, String password) {
		Statement username_statement;
		Statement password_statement;

		try {

			username_statement = connection.createStatement();

			ResultSet username_result = username_statement
					.executeQuery("SELECT username FROM player WHERE username = '" + username + "';");

			if (!username_result.next()) {
				System.out.println("ResultSet empty! No players registered with that username!");
				return false;
			} else {

				password_statement = connection.createStatement();

				ResultSet password_result = password_statement
						.executeQuery("SELECT password FROM player WHERE password = '" + password + "';");

				if (!password_result.next()) {
					System.out.println("Wrong password!");
					return false;
				} else
					return true;

			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	public Boolean editPlayerPassword(String username, String new_password) {

		Statement edit;

		try {

			edit = connection.createStatement();

			edit.executeUpdate("UPDATE player SET password='" + new_password + "' WHERE username='" + username + "';");

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	public Boolean editPlayerName(String username, String new_name) {

		Statement edit;

		try {

			edit = connection.createStatement();

			edit.executeUpdate("UPDATE player SET name='" + new_name + "' WHERE username='" + username + "';");

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}
	
	public Boolean editPlayerEmail(String username, String new_email) {

		Statement edit;

		try {

			edit = connection.createStatement();

			edit.executeUpdate("UPDATE player SET email='" + new_email + "' WHERE username='" + username + "';");

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}
	
	public Boolean editPlayerAge(String username, String new_age) {

		Statement edit;

		try {

			edit = connection.createStatement();

			edit.executeUpdate("UPDATE player SET age=" + Integer.parseInt(new_age) + " WHERE username='" + username + "';");

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}
	
	public Boolean editPlayerCountry(String username, String new_country) {

		Statement edit;

		try {

			edit = connection.createStatement();

			edit.executeUpdate("UPDATE player SET country='" + new_country + "' WHERE username='" + username + "';");

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}
	
	public Boolean createPlayer(String username, String password, String name, String email, String age, String country){
		
		Statement edit;
		
		try{
			
			edit = connection.createStatement();
			
			edit.executeUpdate("INSERT INTO Player (username, password, name, email, age, country) VALUES ('" + username
					+ "','" + password + "','" + name
							+ "','" + email + "'," + Integer.parseInt(age) + ",'" + country + "');");
			
			return true;
			
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
		
	}
	
	public Boolean deletePlayer(String username){
		
		Statement delete;
		
		try{
			
			delete = connection.createStatement();
			
			delete.executeUpdate("DELETE FROM player WHERE username = '" + username + "';");
			
			return true;
			
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
		
	}

}
