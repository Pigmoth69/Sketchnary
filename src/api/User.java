package api;

import connection.Database;

public class User {
	
	private Database database;
	private String username;
	private String password;
	private String name;
	private String email;
	private String age;
	private String country;
	
	public User(Database database, String username, String password){
		this.database = database;
		this.username = username;
		this.password = password;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setEmail(String email){
		this.email = email;
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
		
		if(database.playerVerification(username, password))
			return 200;
		return 404;
		
	}
	
	/**
	 * Process a POST request for EDIT operations
	 * @return http response code
	 */
	public int UserPOST(){
		
		Boolean controller = false;
		
		if(name != null){
			database.editPlayerName(username, name);
			controller = true;
		}
		if(email != null){
			database.editPlayerEmail(username, email);
			controller = true;
		}
		if(age != null){
			database.editPlayerAge(username, age);
			controller = true;
		}
		if(country != null){
			database.editPlayerCountry(username, country);
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
		
		return 404;
		
	}
	
	/**
	 * Process a DELETE request for DELETE operations
	 * @return http response code
	 */
	public int UserDELETE(){
		
		return 404;
		
	}
	
}
