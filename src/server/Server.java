package server;

import connection.Database;

public class Server {
	
	public Server(){}
	
	public static void main(String args[]){
	
		Database database = new Database("jdbc:postgresql://localhost:5432/sketchnary", "postgres", "database123");
		
		database.setup();
		
	}
	
}
