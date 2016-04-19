package connection;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
	
	private String host;
	private String username;
	private String password;
	
	private Connection connection;

	public Database(String host, String username, String password){
		this.host = host;
		this.username = username;
		this.password = password;
		
		connection = null;
	}
	
	public void setup(){
		setConnection();
	}
	
	private void setConnection() {
		
		System.out.println("-------- PostgreSQL "
				+ "JDBC Connection Testing ------------");

		try {

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;

		}

		System.out.println("PostgreSQL JDBC Driver Registered!");

		try {

			connection = DriverManager.getConnection(
					host, username,
					password);

		} catch (SQLException e) {

			System.out.println("Connection Failed! ");
			e.printStackTrace();
			return;

		}

		if (connection != null) {
			System.out.println("Connection successful!");
			selectFromDatabase();
		} else {
			System.out.println("Failed to make connection!");
		}
		
	}
	
	public void insertIntoDatabase(){
		// create a Statement from the connection
		Statement statement;
		try {
			statement = connection.createStatement();

			// insert the data
			statement.executeUpdate("INSERT INTO player (username, password, name, email, age, country) " + "VALUES ('username123', 'pass2222', 'Mr. jonas', 'mr@gmail.com', 18, 'Portugal')");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void selectFromDatabase(){
		
		Statement statement;
		
		try{
			
			statement = connection.createStatement();
			
			ResultSet result = statement.executeQuery("SELECT name FROM player");
			
			while(result.next()){
				System.out.println(result.getString("name"));
			}
			
		}catch(SQLException e){
			e.printStackTrace();
		}
		
	}

}
