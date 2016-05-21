package api;

import connection.Database;

public class User {
	
	private Database database;
	
	private String email;
	private String password = null;
	private String name = null;
	private String username = null;
	private String age = null;
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
	
	public void setAge(String age){
		this.age = age;
	}
	
	public void setCountry(String country){
		this.country = country;
	}
	
	/**
	 * Process a GET request for LOGIN operations
	 * @return http response code
	 */
	public int UserGET(){
		
		String status = database.playerVerification(email, password);
		
		if(status.equals("email"))
			return -1;
		else if(status.equals("password"))
			return -2;
		else if(status.equals("true"))
			return 2;
		else
			return 404;
		
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
		if(age != null){
			database.editPlayerAge(email, age);
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
		
		if(database.createPlayer(username, password, name, email, age, country))
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
	
}
