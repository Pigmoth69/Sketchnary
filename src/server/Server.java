package server;

import connection.Database;
import https.HttpsConnection;

public class Server {
	
	public Server(){}
	
	public static void main(String args[]){

		ServerData serverData = new ServerData();
		
		Database database = new Database(serverData, "jdbc:postgresql://localhost:5432/sketchnary", "postgres", "daniel55934837");
		
		database.setup();
		
		database.recoverDatabase();
		
		/*for(int i = 0; i < serverData.getPlayers().size(); i++){
			System.out.println("Player: " + serverData.getPlayers().get(i).getId() + " " + serverData.getPlayers().get(i).getName());
			System.out.println("Friends: ");
			for(int k = 0; k < serverData.getPlayers().get(i).getFriends().size(); k++){
				System.out.println(serverData.getPlayers().get(i).getFriends().get(k).getName());
			}
		}*/
		HttpsConnection c = new HttpsConnection(database);
		c.setup();
				
	}
	
}
