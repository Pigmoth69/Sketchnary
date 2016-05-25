package api;

import java.sql.ResultSet;

import connection.Database;
import utilities.Constants;

public class User {
	
	private Database database;
	
	private String email;
	private String password = null;
	private String name = null;
	private String username = null;
	private String birthdate = null;
	private String country = null;
	
	public User(Database database, String email, String password){
		this.database = database;
		this.email = email;
		this.password = password;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setUsername(String username){
		this.username = username;
	}
	
	public void setBirthdate(String birthdate){
		this.birthdate = birthdate;
	}
	
	public void setCountry(String country){
		this.country = country;
	}
	
	/**
	 * Process a GET request for LOGIN operations
	 * @return http response code
	 */
	public String UserGET(){
		
		String status = database.playerVerification(email, password);
		
		if(status.equals("email"))
			return Constants.ERROR_USER_EMAIL;
		else if(status.equals("password"))
			return Constants.ERROR_USER_PASSWORD;
		else if(status.equals("true"))
			return Constants.OK;
		else
			return Constants.ERROR;
		
	}
	
	/**
	 * Process a POST request for EDIT operations
	 * @return http response code
	 */
	public int UserPOST(){
		
		Boolean controller = false;
		
		if(password != null){
			database.editPlayerPassword(email, password);
			controller = true;
		}
		if(name != null){
			database.editPlayerName(email, name);
			controller = true;
		}
		if(email != null){
			database.editPlayerUsername(email, username);
			controller = true;
		}
		if(birthdate != null){
			database.editPlayerBirthdate(email, birthdate);
			controller = true;
		}
		if(country != null){
			database.editPlayerCountry(email, country);
			controller = true;
		}
		
		if(controller)
			return 200;
		
		return 404;
		
	}
	
	/**
	 * Process a PUT request for REGISTER operations
	 * @return http response code
	 */
	public int UserPUT(){
		
		if(database.createPlayer(username, password, name, email, birthdate, country))
			return 200;
		return 404;
		
	}
	
	/**
	 * Process a DELETE request for DELETE operations
	 * @return http response code
	 */
	public int UserDELETE(){
		
		if(database.deletePlayer(email))
			return 200;
		return 404;
		
	}
	
	public ResultSet getPlayerInfo(String email){
		
		return database.playerInfo(email);
		
	}
	
}
