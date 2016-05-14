package api;

import connection.Database;

public class User {
	
	private Database database;
	private String username;
	private String password;
	
	public User(Database database, String username, String password){
		this.database = database;
		this.username = username;
		this.password = password;
	}
	
	public int UserGET(){
		
		if(database.playerVerification(username, password))
			return 200;
		return 404;
		
	}
	
	public void UserPOST(){
		
	}
	
	public void UserPUT(){
		
		
	}
	
	public void UserDELETE(){
		
	}
	
}
