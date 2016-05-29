package gameEngine;

import java.util.ArrayList;

public class RoomsEngine {
	
	private ArrayList<GameRoom> rooms;
	
	public RoomsEngine(){
		this.rooms = new ArrayList<GameRoom>();
	}
	
	public ArrayList<GameRoom> getRooms(){
		return rooms;
	}
	
	public void addRoom(GameRoom room){
		rooms.add(room);
	}

	public GameRoom findRoom(String room) {
		
		for(int i = 0; i < rooms.size(); i++){
			if(room.equals(rooms.get(i).getID())){
				return rooms.get(i);
			}
		}
		
		return null;
	}

}
